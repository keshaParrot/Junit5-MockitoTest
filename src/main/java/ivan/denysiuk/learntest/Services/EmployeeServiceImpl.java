package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    /**
     * @return
     */
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.getEmployeeById(id);
    }

    /**
     * @param pesel
     * @return
     */
    @Override
    public Employee getEmployeeByPESEL(String pesel) {
        return employeeRepository.getEmployeeByPESEL(pesel);
    }
    /**
     * @param fullName
     * @return
     */
    @Override
    public Employee getEmployeeByfullName(String fullName) {
        return employeeRepository.getEmployeeByFullName(fullName);
    }
    /**
     * @param employee
     * @return
     */
    @Override
    public Employee addEmployeeToDatabase(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * @param employee
     * @return
     */
    @Override
    public Employee updateEmployeeOnDatabase(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean deleteEmployeeFromDatabase(Long id) {
        if (employeeRepository.existsById(id)){
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    @Override
    public int countAllEmployees() {
        return (int) employeeRepository.count();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public double getMonthSalary(Long id) {
        Employee employee = employeeRepository.getEmployeeById(id);
        double totalSalary = 0.0;

        if(employee.getWorkedShift() == null || employee.getWorkedShift().isEmpty()){
            return totalSalary;
        }
        totalSalary = employee.getWorkedShift().stream()
                .mapToDouble(shift -> {
                    double hours = shift.getWorkedTime().getHours();
                    double minutes = shift.getWorkedTime().getMinutes();
                    return (hours + minutes / 60.0) * employee.getRate();
                })
                .sum();

        return totalSalary;
    }
    @Override
    public Map<String, Double> getMonthTax(Long id) {
        double employeeSalary = getMonthSalary(id);

        Map<String,Double> mapOfTax = new HashMap<>();

        double SSC = getPercentage(employeeSalary,13.71);
        mapOfTax.put("social security contributions",SSC);

        double HIS = getPercentage(employeeSalary-SSC,9);
        mapOfTax.put("Health insurance contribution",HIS);

        double PPK = getPercentage(employeeSalary,2);
        mapOfTax.put("PPK",PPK);

        double baseForMainTax = Math.round(employeeSalary+getPercentage(employeeSalary,1.5)-SSC-250.0);
        baseForMainTax = getPercentage(baseForMainTax,12);
        mapOfTax.put("advance income tax",baseForMainTax);

        double mainTax = Math.round(baseForMainTax-300.0);
        mapOfTax.put("Tax",mainTax);

        return mapOfTax;
    }
    private double getPercentage(double total, double percent) {
        return total * (percent / 100.0);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public double getMonthRevenue(Long id) {
        double employeeSalary = getMonthSalary(id);
        Map<String,Double> mapOfTax = getMonthTax(id);

        double totalTax = mapOfTax.values().stream().mapToDouble(Double::doubleValue).sum();

        return employeeSalary - totalTax;
    }
}
