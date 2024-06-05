package ivan.denysiuk.learntest.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;

public class CustomConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final EmployeeExistValidator employeeExistValidator;

    public CustomConstraintValidatorFactory(EmployeeExistValidator employeeExistValidator) {
        this.employeeExistValidator = employeeExistValidator;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        if (key == EmployeeExistValidator.class) {
            return (T) employeeExistValidator;
        }
        try {
            return key.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create an instance of " + key, e);
        }
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        // No-op
    }
}
