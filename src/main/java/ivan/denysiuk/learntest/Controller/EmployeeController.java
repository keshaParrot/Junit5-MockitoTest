package ivan.denysiuk.learntest.Controller;

import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee/")
public class EmployeeController {
    private final EmployeeService employeeService;

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
    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id")Long id){
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //@GetMapping("{id}/salary")
    //public ResponseEntity<Employee> getEmployeeSalary(@RequestAttribute("id")Long id){

    //}
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
     * @return
     */
    @DeleteMapping("{id}/delete")
    public ResponseEntity deleteEmployee(@PathVariable("id")Long id){
        boolean deleted = employeeService.deleteEmployeeFromDatabase(id);
        if(deleted) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee){
        Employee savedEmployee = employeeService.addEmployeeToDatabase(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    /**
     *
     * @return
     */
    @PutMapping@PostMapping("/update")
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee){
        Employee updatedEmployee = employeeService.updateEmployeeOnDatabase(employee);
        if(updatedEmployee != null) {
            return new ResponseEntity<>(updatedEmployee, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @return
     */
    @PatchMapping
    public ResponseEntity<Employee> updatePatchEmployee(){
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

}
