package kr.co.yeogiga.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValidation, Enum<?>> {
    private Set<Enum<?>> enums;
    
    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.enums = Arrays.stream(constraintAnnotation.target().getEnumConstants())
                .collect(Collectors.toSet());
    }
    
    @Override
    public boolean isValid(Enum<?> anEnum, ConstraintValidatorContext constraintValidatorContext) {
        if (anEnum == null) {
            return false;
        }
        
        return enums.contains(anEnum);
    }
}
