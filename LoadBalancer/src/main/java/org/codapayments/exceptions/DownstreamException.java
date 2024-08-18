package org.codapayments.exceptions;

public class DownstreamException extends RuntimeException {
    public DownstreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
