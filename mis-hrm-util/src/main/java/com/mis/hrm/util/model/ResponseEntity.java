package com.mis.hrm.util.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseEntity<T>{
    private int status;
    private String message;
    private T object;
}
