package com.mis.hrm.util.validation;

import com.mis.hrm.util.exception.ParameterException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationUtil {
    public static void checkBindingResult(BindingResult result){
        if (result.hasErrors()){
            List<ObjectError> errors = result.getAllErrors();
            throw new ParameterException(errors.get(0).getDefaultMessage());
        }
    }
}
