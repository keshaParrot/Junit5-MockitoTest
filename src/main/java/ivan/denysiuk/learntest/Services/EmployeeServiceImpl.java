package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Service giving access to employee entity repository
 * Service implement different methods for getting, saving, update employee to database.
 * Service also get access to methods which calculate employee revenue, salary, and tax.
 */
@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    /**
     * Find all employee on database
     *
     * @return list of all employee
     */
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Search employee by id
     *
     * @param id of the searched employee
     * @return searched employee
     */
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.getEmployeeById(id);
    }

    /**
     * Search employee by PESEL
     *
     * @param pesel of the searched employee
     * @return searched employee
     */
    @Override
    public Employee getEmployeeByPESEL(String pesel) {
        return employeeRepository.getEmployeeByPESEL(pesel);
    }
    /**
     * Searching employee by full name
     *
     * @param fullName of the searched employee
     * @return the searched employee
     */
    @Override
    public Employee getEmployeeByfullName(String fullName) {
        return employeeRepository.getEmployeeByFullName(fullName);
    }
    /**
     * Add employee to database
     *
     * @param employee which we need to save
     * @return instance of saved employee
     */
    @Override
    public Employee addEmployeeToDatabase(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Update employee on database
     *
     * @param employee which we need to update
     * @return instance of updated employee
     */
    @Override
    public Employee updateEmployeeOnDatabase(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Delete employee on database
     *
     * @param id of the employee which we need to delete
     * @return TRUE if employee with id exist od DB, and delete successfully,
     *         FALSE if employee with id not exist od DB, and delete failed
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
     * Count all employee records on employee table
     *
     * @return numbers of employee
     */
    @Override
    public int countAllEmployees() {
        return (int) employeeRepository.count();
    }

    /**
     * Calculates months salary of the selected employee
     *
     * @param id of the selected employee
     * @param month the month for which the salary should be calculated
     * @return month salary of selected employee
     */
    @Override
    public double getMonthSalary(Long id, int month) {
        Employee employee = employeeRepository.getEmployeeById(id);
        double totalSalary = 0.0;

        if(employee.getWorkedShift() == null || employee.getWorkedShift().isEmpty()){
            return totalSalary;
        }

        totalSalary = employee.getWorkedShift().stream()
                .filter(shift -> {
                    int shiftMonth = shift.getDate().getMonth().getValue();
                    System.out.println(shiftMonth);
                    return shiftMonth == month;
                })
                .mapToDouble(shift -> {
                    double hours = shift.getWorkedTime().getHours();
                    double minutes = shift.getWorkedTime().getMinutes();
                    return (hours + minutes / 60.0) * employee.getRate();
                })
                .sum();

        return totalSalary;
    }

    /**
     * Calculates months Tax of the selected employee
     *
     * @param id of the selected employee
     * @param month the month for which the tax should be calculated
     * @return Map of the all taxes selected by the employee id
     */
    @Override
    public Map<String, Double> getMonthTax(Long id, int month) {
        Map<String,Double> mapOfTax = new HashMap<>();

        double employeeSalary = getMonthSalary(id,month);

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
    /**
     * Calculate percent of the total
     *
     * @param total from which we want to calculate percent
     * @param percent
     * @return percent of the total
     */
    private double getPercentage(double total, double percent) {
        return total * (percent / 100.0);
    }

    /**
     * Calculates months revenue of the selected employee
     *
     * @param id of the selected employee
     * @param month the month for which the revenue should be calculated
     * @return revenue of the selected employee
     */
    @Override
    public double getMonthRevenue(Long id, int month) {
        double employeeSalary = getMonthSalary(id, month);
        Map<String,Double> mapOfTax = getMonthTax(id, month);

        double totalTax = mapOfTax.values().stream().mapToDouble(Double::doubleValue).sum();

        return employeeSalary - totalTax;
    }
}
