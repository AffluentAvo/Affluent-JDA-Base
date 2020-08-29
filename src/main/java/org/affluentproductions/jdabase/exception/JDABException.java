package org.affluentproductions.jdabase.exception;

public class JDABException extends Exception {

    private final String message;

    public JDABException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}