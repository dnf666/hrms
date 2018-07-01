package com.facishare.crm.customeraccount.predefine.manager.result;

public class FuncResult<T> {
    private Integer code;
    private String msg;
    private T result;

    public boolean isSuccess() {
        if (code == null) {
            return false;
        } else if (code.intValue() == 0) {
            return true;
        }
        return false;
    }
}
