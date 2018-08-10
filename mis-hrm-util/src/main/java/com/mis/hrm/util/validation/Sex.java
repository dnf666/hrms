package com.mis.hrm.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE,ElementType.METHOD,ElementType.FIELD,ElementType.CONSTRUCTOR,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {SexValidator.class})
public @interface Sex {

    String message() default "性别只能是男或者女";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
