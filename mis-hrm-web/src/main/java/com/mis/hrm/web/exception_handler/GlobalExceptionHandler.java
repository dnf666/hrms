package com.mis.hrm.web.exception_handler;

import com.mis.hrm.login.exception.AuthorizationException;
import com.mis.hrm.util.exception.ParameterException;
import com.mis.hrm.util.model.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
/**
 * @author May
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity handleIOException(IOException e){
        return new ResponseEntity<>(500, e.getMessage(), "");
    }

    @ExceptionHandler(ParameterException.class)
    public ResponseEntity handleParameterException(ParameterException e) {
        return new ResponseEntity<>(400, e.getMessage(), "");
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity handleAuthorizationException(AuthorizationException e){
        return new ResponseEntity<>(401, e.getMessage(), "");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){
        return new ResponseEntity<>(500, "未知错误", "");
    }
}
