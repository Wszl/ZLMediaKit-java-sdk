package org.xdove.media.zlk.exception;

public class RespErrorException extends RuntimeException {

    public RespErrorException() {
    }

    public RespErrorException(String message) {
        super(message);
    }

    public RespErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RespErrorException(Throwable cause) {
        super(cause);
    }

    public RespErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
