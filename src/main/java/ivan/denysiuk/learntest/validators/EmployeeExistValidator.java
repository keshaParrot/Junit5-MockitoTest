package ivan.denysiuk.learntest.validators;

import ivan.denysiuk.learntest.services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.annotations.EmployeeExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeExistValidator implements ConstraintValidator<EmployeeExist,Long> {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeExistValidator(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    /**
     * @param employeeId id of employee which we want to check on exist
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Long employeeId, ConstraintValidatorContext constraintValidatorContext) {
        if (employeeId == null) {
            return true;
        }
        return employeeService.existById(employeeId);
    }
}
