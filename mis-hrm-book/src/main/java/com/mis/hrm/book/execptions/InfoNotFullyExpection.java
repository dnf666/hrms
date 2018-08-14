package com.mis.hrm.book.execptions;

public class InfoNotFullyExpection extends Exception{
    private String message;

    public InfoNotFullyExpection(String message, String message1) {
        super(message);
        this.message = message1;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
