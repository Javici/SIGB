package com.library.sigb.service;

import com.library.sigb.entity.Loan;
import com.library.sigb.entity.Reservation;
import com.library.sigb.entity.ReservationStatus;
import com.library.sigb.repository.ReservationRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * Servicio de notificaciones por correo electrónico.
 *
 * Aplica Structured Concurrency (JEP 505, Java 25) para orquestar
 * en paralelo tres tareas independientes tras cada devolución:
 *
 *  1. Actualizar el estado de la reserva en BBDD → NOTIFIED
 *  2. Enviar el email al primer usuario de la cola
 *  3. Registrar el evento de auditoría (log)
 *
 * Las tres tareas se tratan como una unidad de trabajo: si alguna falla,
 * el scope cancela las demás (ShutdownOnFailure) y lanza la excepción.
 * Gracias a los Virtual Threads, ninguna tarea bloquea hilo de plataforma
 * durante la espera de I/O (SMTP, JDBC).
 *
 * El método es @Async para no bloquear la petición HTTP del bibliotecario.
 */
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final ReservationRepository reservationRepository;

    public NotificationService(@NonNull JavaMailSender mailSender,
                               @NonNull ReservationRepository reservationRepository) {
        this.mailSender = mailSender;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Notifica al primer usuario en cola cuando se registra una devolución.
     * Usa Structured Concurrency para ejecutar las tres tareas en paralelo.
     */
    @Async("virtualThreadExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyNextInQueue(@NonNull Loan returnedLoan) {
        Long bookId = returnedLoan.getBook().getId();

        reservationRepository.findFirstInQueue(bookId).ifPresent(reservation -> {
            try {
                CompletableFuture.allOf(
                        CompletableFuture.runAsync(() -> markReservationNotified(reservation)),
                        CompletableFuture.runAsync(() -> sendAvailabilityEmail(reservation)),
                        CompletableFuture.runAsync(() -> logAuditEvent(returnedLoan, reservation))
                ).join();
            } catch (Exception e) {
                System.err.println("[SIGB] Error en notificación de cola: " + e.getMessage());
            }
        });
    }

    // ── Tareas individuales (ejecutadas en Virtual Threads hijos) ─────

    private void markReservationNotified(@NonNull Reservation r) {
        r.setStatus(ReservationStatus.NOTIFIED);
        reservationRepository.save(r);
    }

    private void sendAvailabilityEmail(@NonNull Reservation r) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(r.getUser().getEmail());
        msg.setSubject("[SIGB] Libro disponible: " + r.getBook().getTitle());
        msg.setText("""
                Hola %s,

                Te informamos de que el libro "%s" que tenías reservado
                ya está disponible para ser recogido en la biblioteca.

                Tu posición en la cola era: %d
                Tienes 48 horas para recogerlo antes de que pase al siguiente usuario.

                Un saludo,
                Sistema SIGB – Biblioteca
                """.formatted(
                r.getUser().getUsername(),
                r.getBook().getTitle(),
                r.getQueuePosition()
        ));
        mailSender.send(msg);
    }

    private void logAuditEvent(@NonNull Loan loan, @NonNull Reservation reservation) {
        System.out.printf("[AUDIT] Devolución loanId=%d → notificación enviada a userId=%d para bookId=%d%n",
                loan.getId(), reservation.getUser().getId(), reservation.getBook().getId());
    }
}
