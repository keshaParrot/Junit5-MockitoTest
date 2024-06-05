package ivan.denysiuk.learntest.services.interfaces;

import ivan.denysiuk.learntest.domains.dtos.EmployeeDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface EmployeeService {
    Page<Employee> getAllEmployees(int numberOfElement, int numberOfPage);
    Employee getEmployeeById(Long id);
    List<Employee> findByParams(String pesel, String fullName, String specialization);
    Employee saveEmployee(EmployeeDto employee);
    Employee updateEmployee(Long employeeId, EmployeeDto updatedEmployee);
    Employee patchEmployee(Long employeeId, EmployeeDto updatedEmployee);
    boolean deleteEmployee(Long id);
    int countAllEmployees();
    double getMonthSalary(Long id, int month);
    Map<String, Double> getMonthTax(Long id, int month);
    double getMonthRevenue(Long id, int month);
    boolean existById(Long employeeId);
}
