package ivan.denysiuk.learntest.annotations;

import ivan.denysiuk.learntest.validators.EmployeeExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmployeeExistValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmployeeExist {
    String message() default "Employee does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
