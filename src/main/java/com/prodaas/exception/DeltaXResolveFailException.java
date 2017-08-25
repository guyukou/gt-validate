package com.prodaas.exception;

/**
 * @author guyu
 */
public class DeltaXResolveFailException extends Exception{
    public DeltaXResolveFailException() {
    }

    public DeltaXResolveFailException(String message) {
        super(message);
    }

    public DeltaXResolveFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeltaXResolveFailException(Throwable cause) {
        super(cause);
    }

    public DeltaXResolveFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
