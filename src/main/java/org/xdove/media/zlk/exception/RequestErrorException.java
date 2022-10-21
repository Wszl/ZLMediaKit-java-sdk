package org.xdove.media.zlk.exception;

public class RequestErrorException extends RuntimeException {
    public RequestErrorException() {
    }

    public RequestErrorException(String message) {
        super(message);
    }

    public RequestErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestErrorException(Throwable cause) {
        super(cause);
    }

    public RequestErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
