package com.mis.hrm.util.exception;

/**
 * @author May
 */
public class ParameterException extends RuntimeException {
    public ParameterException() {
    }

    public ParameterException(String msg) {
        super(msg);
    }

    public ParameterException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ParameterException(Throwable throwable) {
        super(throwable);
    }
}
