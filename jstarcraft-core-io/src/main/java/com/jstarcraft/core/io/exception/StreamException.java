package com.jstarcraft.core.io.exception;

public class StreamException extends RuntimeException {

    private static final long serialVersionUID = 6526127733069513446L;

    public StreamException() {
        super();
    }

    public StreamException(String message, Throwable exception) {
        super(message, exception);
    }

    public StreamException(String message) {
        super(message);
    }

    public StreamException(Throwable exception) {
        super(exception);
    }
}
