package kr.co.yeogiga.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {EnumValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidation {
    String message() default "지원하지 않는 Enum 값입니다.";
    
    Class<? extends Payload>[] payload() default {};
    
    Class<?>[] groups() default {};
    
    Class<? extends java.lang.Enum<?>> target();
}
