package com.mis.hrm.util.exception;

public class InfoNotFullyExpection extends Exception{
    private String message;

    public InfoNotFullyExpection(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
