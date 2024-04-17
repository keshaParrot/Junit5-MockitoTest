package ivan.denysiuk.learntest.Services.interfaces;

import ivan.denysiuk.learntest.domain.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee getEmployeeByPESEL(String pesel);

    Employee getEmployeeByfullName(String fullName);

    Employee addEmployeeToDatabase(Employee employee);
    Employee updateEmployeeOnDatabase(Employee employee);
    boolean deleteEmployeeFromDatabase(Long id);
    int countAllEmployees();
    double getMonthSalary(Long id);
    double getMonthRevenue(Long id);
    Map<String,Double> getMonthTax(Long id);
}
