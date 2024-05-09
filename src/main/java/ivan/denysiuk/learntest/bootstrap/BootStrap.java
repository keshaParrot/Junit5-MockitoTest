package ivan.denysiuk.learntest.bootstrap;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.ShiftServiceImpl;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class BootStrap implements CommandLineRunner {

    EmployeeRepository employeeRepository;
    ShiftRepository shiftRepository;
    EmployeeService employeeService;
    ShiftServiceImpl shiftService;

    @Override
    public void run(String... args){
        Employee employee = Employee.builder()
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        Stream<Shift> randomShifts = Stream.generate(() -> {
            return Shift.builder()
                    .station("Station " + (new Random().nextInt(10) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime("10:00")
                    .endTime("20:00")
                    .actualStartTime("10:00")
                    .actualEndTime("20:00")
                    .employee(employeeRepository.getEmployeeById(savedEmployee.getId()))
                    .build();
        }).limit(5);

        List<Shift> savedShifts = shiftRepository.saveAll(randomShifts.collect(Collectors.toList()));


        System.out.println(employeeService.getAllEmployees());
        System.out.println("salary= "+employeeService.getMonthSalary(1L,5));
        System.out.println("tax= "+employeeService.getMonthTax(1L,5));
        System.out.println("Revenue= "+employeeService.getMonthRevenue(1L,5));
    }
}
