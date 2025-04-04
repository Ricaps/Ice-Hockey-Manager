package cz.fi.muni.pa165.enums.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

	private Enum<?>[] enumValues;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		enumValues = constraintAnnotation.enumClass().getEnumConstants();
	}

	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}

		for (Enum<?> enumConstant : enumValues) {
			if (enumConstant.equals(value)) {
				return true;
			}
		}

		return false;
	}

}
