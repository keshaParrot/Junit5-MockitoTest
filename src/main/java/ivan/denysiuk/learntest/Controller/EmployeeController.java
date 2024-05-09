package ivan.denysiuk.learntest.Controller;

import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.dto.EmployeeDto;
import ivan.denysiuk.learntest.domain.dto.SalaryDto;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.mapper.EmployeeMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 *
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {
    public static final String EMPLOYEE_PATH = "/api/v1/employee";
    public static final String EMPLOYEE_PATH_ID = EMPLOYEE_PATH + "/{id}";
    private final EmployeeService employeeService;

    /**
     *
     * @param id
     * @return
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
    @GetMapping(EMPLOYEE_PATH_ID+"/salary")
    public ResponseEntity<SalaryDto> getEmployeeSalary(@PathVariable Long id,
                                               @RequestParam(name = "month") Integer month) {

        System.out.println(id +" "+ month);
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        if (employee.getWorkedShift().isEmpty()) {
            return ResponseEntity.badRequest()
                    .header("Error", "Employee does not have any shifts")
                    .build();
        }

        Map<String, Double> tax = employeeService.getMonthTax(id, month);
        double salary = employeeService.getMonthSalary(id, month);

        SalaryDto salaryDto = new SalaryDto(id, employee.getFullName(), tax, salary);
        return ResponseEntity.ok(salaryDto);
    }

    /**
     *
     * @param pesel
     * @return
     */
    @GetMapping(EMPLOYEE_PATH+"/searchbypesel/{PESEL}")
    public ResponseEntity<EmployeeDto> getEmployeeByPESEL(@PathVariable("PESEL") String pesel){
        Employee employee = employeeService.getEmployeeByPESEL(pesel);
        if (employee != null) {
            return ResponseEntity.ok(getDtoFromEmployee(employee));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param fullName
     * @return
     */
    @GetMapping(EMPLOYEE_PATH+"/searchbyfullname/{fullName}")
    public ResponseEntity<EmployeeDto> getEmployeeByFullName(@PathVariable("fullName") String fullName){
        Employee employee = employeeService.getEmployeeByfullName(fullName);
        if (employee != null) {
            return ResponseEntity.ok(getDtoFromEmployee(employee));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping(EMPLOYEE_PATH_ID+"/delete")
    public ResponseEntity deleteEmployee(@PathVariable("id")Long id){
        boolean deleted = employeeService.deleteEmployee(id);
        if(deleted) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param employee
     * @param bindingResult
     * @return
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
     *
     * @param employeeId
     * @param employee
     * @param bindingResult
     * @return
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
     *
     * @param employeeId
     * @param employee
     * @return
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
     *
     * @param employeeDto
     * @return
     */
    private Employee getEmployeeFromDto(EmployeeDto employeeDto){
        return EmployeeMapper.INSTANCE.dtoToEmployee(employeeDto);
    }
    private EmployeeDto getDtoFromEmployee(Employee employee){
        return EmployeeMapper.INSTANCE.employeeToDto(employee);
    }
}
