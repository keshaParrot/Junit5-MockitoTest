package ivan.denysiuk.learntest.controllers;

import ivan.denysiuk.learntest.services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domains.dtos.EmployeeDto;
import ivan.denysiuk.learntest.domains.dtos.SalaryDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import ivan.denysiuk.learntest.domains.mappers.EmployeeMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing employees.
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {
    public static final String EMPLOYEE_PATH = "/api/v1/employee";
    public static final String EMPLOYEE_PATH_ID = EMPLOYEE_PATH + "/{id}";
    private final EmployeeService employeeService;

    /**
     * Retrieves a paginated list of employees.
     *
     * @param numberOfPage the number of the page to retrieve
     * @param pageSize the size of the page to retrieve
     * @return a ResponseEntity containing a list of EmployeeDto objects
     */
    @GetMapping(EMPLOYEE_PATH)
    public ResponseEntity<List<EmployeeDto>> getAllEmployee(@RequestParam("numberofpage") Optional<Integer> numberOfPage,
                                                            @RequestParam("pagesize") Optional<Integer> pageSize){
        List<Employee> allEmployees;
        if (numberOfPage.isPresent() && pageSize.isPresent()){
            allEmployees = employeeService.getAllEmployees(numberOfPage.get(),pageSize.get()).getContent();

            System.out.println();
            if (!allEmployees.isEmpty()) {
                List<EmployeeDto> employeeDtos = allEmployees.stream()
                        .map(this::getDtoFromEmployee)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(employeeDtos);
            }

            return ResponseEntity.notFound().build();
        }
        return buildErrorResponse("Params cannot be null",HttpStatus.BAD_REQUEST,null);
    }
    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to retrieve
     * @return a ResponseEntity containing the EmployeeDto object
     */
    @GetMapping(EMPLOYEE_PATH_ID)
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id")Long id){
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(getDtoFromEmployee(employee));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Retrieves the salary information of an employee for a given month.
     *
     * @param id the ID of the employee
     * @param month the month for which to retrieve the salary information
     * @return a ResponseEntity containing the SalaryDto object
     */
    @GetMapping(EMPLOYEE_PATH_ID+"/salary")
    public ResponseEntity<SalaryDto> getEmployeeSalary(@PathVariable Long id,
                                                       @RequestParam(name = "month") Integer month) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest()
                    .header("Error", "Invalid month value. Month should be between 1 and 12.")
                    .build();
        }
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound()
                    .header("Error", "Employee does not exist")
                    .build();
        }
        if (employee.getWorkedShift() == null || employee.getWorkedShift().isEmpty()) {
            return ResponseEntity.notFound()
                    .header("Error", "Employee does not have any shifts")
                    .build();
        }

        Map<String, Double> tax = employeeService.getMonthTax(id, month);
        double salary = employeeService.getMonthSalary(id, month);
        if (salary == 0.0 || tax.isEmpty()){
            return ResponseEntity.notFound()
                    .header("Error", "Employee does not have any shifts on this months")
                    .build();
        }

        SalaryDto salaryDto = new SalaryDto(id, employee.getFullName(), tax, salary);
        return ResponseEntity.ok(salaryDto);
    }

    /**
     * Retrieves an employee by their PESEL.
     *
     * @param pesel the PESEL of the employee to retrieve
     * @return a ResponseEntity containing the EmployeeDto object
     */
    @GetMapping(EMPLOYEE_PATH+"/searchby")
    public ResponseEntity<List<EmployeeDto>> getEmployeeBy(@RequestParam(value = "pesel",required = false) Optional<String> pesel,
                                                           @RequestParam(value = "fullname",required = false) Optional<String> fullName,
                                                           @RequestParam(value = "specialization",required = false) Optional<String> specialization){

        if (pesel.isPresent() || fullName.isPresent() || specialization.isPresent()){
            List<Employee> employees = employeeService.findByParams(pesel.orElse(null),fullName.orElse(null),specialization.orElse(null));

            if (!employees.isEmpty()) {
                List<EmployeeDto> response = employees
                        .stream()
                        .map(this::getDtoFromEmployee)
                        .toList();
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        }
       return buildErrorResponse("Attributes cannot be empty",HttpStatus.BAD_REQUEST,null);

    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @DeleteMapping(EMPLOYEE_PATH_ID+"/delete")
    public ResponseEntity deleteEmployee(@PathVariable("id")Long id){
        boolean deleted = employeeService.deleteEmployee(id);
        if(deleted) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a new employee.
     *
     * @param employee the EmployeeDto object containing the details of the employee to create
     * @param bindingResult the result of the validation of the employee details
     * @return a ResponseEntity containing the created EmployeeDto object
     */
    @PostMapping(EMPLOYEE_PATH+"/create")
    public ResponseEntity<EmployeeDto> saveEmployee(@Valid @RequestBody EmployeeDto employee,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(employee);
        }

        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(getDtoFromEmployee(savedEmployee), HttpStatus.CREATED);
    }

    /**
     * Updates an existing employee by their ID.
     *
     * @param employeeId the ID of the employee to update
     * @param employee the EmployeeDto object containing the updated details of the employee
     * @param bindingResult the result of the validation of the employee details
     * @return a ResponseEntity containing the updated EmployeeDto object
     */
    @PutMapping(EMPLOYEE_PATH_ID+"/update")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long employeeId,
                                                   @Valid @RequestBody EmployeeDto employee,
                                                   BindingResult bindingResult){

        Employee updatedEmployee = employeeService.updateEmployee(employeeId,employee);


        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(employee);
        }
        if(updatedEmployee != null) {
            return new ResponseEntity<>(getDtoFromEmployee(updatedEmployee), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Partially updates an existing employee by their ID.
     *
     * @param employeeId the ID of the employee to update
     * @param employee the EmployeeDto object containing the updated details of the employee
     * @return a ResponseEntity containing the updated EmployeeDto object
     */
    @PatchMapping(EMPLOYEE_PATH_ID+ "/update")
    public ResponseEntity<EmployeeDto> PatchEmployee(@PathVariable("id") Long employeeId,
                                                         @RequestBody EmployeeDto employee){

        Employee updatedEmployee = employeeService.patchEmployee(employeeId,employee);
        if(updatedEmployee != null) {
            return new ResponseEntity<>(getDtoFromEmployee(updatedEmployee), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Builds an error response with the given error message, status, and body.
     *
     * @param errorMessage the error message to include in the response
     * @param status the HTTP status of the response
     * @param body the body of the response
     * @param <T> the type of the body
     * @return a ResponseEntity containing the error message and status
     */
    private <T> ResponseEntity<T> buildErrorResponse(String errorMessage, HttpStatus status, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Errors", errorMessage);
        if (body != null) {
            return new ResponseEntity<>(body, headers, status);
        }
        return new ResponseEntity<>(headers, status);
    }
    /**
     * Converts an Employee entity to an EmployeeDto object.
     *
     * @param employee the Employee entity to convert
     * @return the corresponding Employee object
     */
    private EmployeeDto getDtoFromEmployee(Employee employee){
        return EmployeeMapper.INSTANCE.employeeToDto(employee);
    }
}
