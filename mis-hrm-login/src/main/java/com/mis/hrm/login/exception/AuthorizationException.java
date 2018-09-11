package com.mis.hrm.login.exception;

public class AuthorizationException extends RuntimeException{
    public AuthorizationException() {
    }

    public AuthorizationException(String msg) {
        super(msg);
    }

    public AuthorizationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public AuthorizationException(Throwable throwable) {
        super(throwable);
    }
}
