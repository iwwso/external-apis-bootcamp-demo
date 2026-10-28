package com.accenture.externalapis.demo.client;

/**
 * Wraps any failure of the external book service, so that framework-specific
 * exceptions never leak out of the client layer.
 *
 * @author Karyna Litvinova
 */
public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}