package com.library.sigb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configura el executor para @Async con Virtual Threads (JEP 425, Java 25).
 *
 * SimpleAsyncTaskExecutor con setVirtualThreads(true) crea un nuevo Virtual Thread
 * por cada tarea asíncrona, en lugar de usar un pool de plataforma. Con Virtual Threads
 * el bloqueo en I/O (consultas JPA, envío de email) no bloquea el hilo de la plataforma
 * subyacente, lo que permite escalar a miles de tareas concurrentes con mínima memoria.
 *
 * Nota: spring.threads.virtual.enabled=true ya configura Tomcat 11 con Virtual Threads;
 * esta clase complementa el comportamiento para los métodos @Async de los servicios.
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "virtualThreadExecutor")
    public Executor getAsyncExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-thread-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
