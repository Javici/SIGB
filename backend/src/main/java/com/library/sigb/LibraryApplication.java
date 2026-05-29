package com.library.sigb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Punto de entrada principal del SIGB.
 *
 * Spring Boot 4.0.6 detecta automáticamente:
 *   – spring.threads.virtual.enabled=true  → Tomcat 11 usa Virtual Threads (JEP 425)
 *   – @EnableAsync                          → métodos @Async también usan Virtual Threads
 */
@SpringBootApplication
@EnableAsync
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
