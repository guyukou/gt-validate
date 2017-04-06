package com.prodaas.exception;

/**
 * Created by guyu on 2017/4/6.
 */
public class IPLimitException extends Exception{
    public IPLimitException() {
    }

    public IPLimitException(String message) {
        super(message);
    }

    public IPLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public IPLimitException(Throwable cause) {
        super(cause);
    }

    public IPLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
