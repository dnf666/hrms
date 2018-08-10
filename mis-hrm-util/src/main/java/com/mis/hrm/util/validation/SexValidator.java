package com.mis.hrm.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author May
 */
public class SexValidator implements ConstraintValidator<Sex,String> {


    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.equals("男") || s.equals("女")){
            return true;
        }
        return false;
    }
}
