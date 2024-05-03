package ivan.denysiuk.learntest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    /**
     * Send ResponseEntity if not empty with code 200 (ok)
     *
     * @return ResponseEntity with code 200 if employees exist
     *         ResponseEntity with code 404 if employees not exist
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employees = employeeService.getAllEmployees();
        if (!employees.isEmpty()) {
            return ResponseEntity.ok(employees);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id")Long id){
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("{id}/salary")
    public ResponseEntity<?> getEmployeeSalary(@PathVariable Long id,
                                               @RequestParam(name = "month") Integer month) {
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
    @GetMapping("/searchbypesel/{PESEL}")
    public ResponseEntity<Employee> getEmployeeByPESEL(@PathVariable("PESEL") String pesel){
        Employee employee = employeeService.getEmployeeByPESEL(pesel);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param fullName
     * @return
     */
    @GetMapping("/searchbyfullname/{fullName}")
    public ResponseEntity<Employee> getEmployeeByFullName(@PathVariable("fullName") String fullName){
        Employee employee = employeeService.getEmployeeByfullName(fullName);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}/delete")
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
    @PostMapping("/create")
    public ResponseEntity<Employee> saveEmployee(@Valid @RequestBody EmployeeDto employee,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(getEmployeeFromDto(employee));
        }

        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    /**
     *
     * @param employeeId
     * @param employee
     * @param bindingResult
     * @return
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long employeeId,
                                                   @Valid @RequestBody EmployeeDto employee,
                                                   BindingResult bindingResult){

        employee.setId(employeeId);
        Employee updatedEmployee = employeeService.updateEmployee(employeeId,employee);


        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(getEmployeeFromDto(employee));
        }
        if(updatedEmployee != null) {
            return new ResponseEntity<>(updatedEmployee, HttpStatus.ACCEPTED);
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
    @PatchMapping("/{id}/update")
    public ResponseEntity<Employee> updatePatchEmployee(@PathVariable("id") Long employeeId,
                                                         @RequestBody EmployeeDto employee){

        Employee updatedEmployee = employeeService.patchEmployee(employeeId,employee);
        if(updatedEmployee != null) {
            return new ResponseEntity<>(updatedEmployee, HttpStatus.ACCEPTED);
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
        return EmployeeMapper.INSTANCE.DtoToEmployee(employeeDto);
    }
}
