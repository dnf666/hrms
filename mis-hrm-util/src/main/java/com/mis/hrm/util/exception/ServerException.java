package com.mis.hrm.util.exception;

/**
 * @author May
 */
public class ServerException extends RuntimeException {

    public ServerException() {
    }

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }

}
