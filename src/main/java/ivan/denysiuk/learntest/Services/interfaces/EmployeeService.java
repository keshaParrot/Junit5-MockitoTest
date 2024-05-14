package ivan.denysiuk.learntest.Services.interfaces;

import ivan.denysiuk.learntest.domain.dto.EmployeeDto;
import ivan.denysiuk.learntest.domain.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface EmployeeService {
    Page<Employee> getAllEmployees(int numberOfElement, int numberOfPage);
    Employee getEmployeeById(Long id);
    Employee getEmployeeByPESEL(String pesel);
    Employee getEmployeeByfullName(String fullName);
    Employee saveEmployee(EmployeeDto employee);
    Employee updateEmployee(Long employeeId, EmployeeDto updatedEmployee);
    Employee patchEmployee(Long employeeId, EmployeeDto updatedEmployee);
    boolean deleteEmployee(Long id);
    int countAllEmployees();
    double getMonthSalary(Long id, int month);
    Map<String, Double> getMonthTax(Long id, int month);
    double getMonthRevenue(Long id, int month);
}
