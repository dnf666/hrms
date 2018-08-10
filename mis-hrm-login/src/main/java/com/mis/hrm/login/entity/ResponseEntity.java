package com.mis.hrm.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseEntity<T>{
    private int status;
    private String message;
    private T object;
}
