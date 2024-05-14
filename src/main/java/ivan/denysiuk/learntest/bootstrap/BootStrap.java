package ivan.denysiuk.learntest.bootstrap;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.ShiftServiceImpl;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.HoursClass;
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
    ShiftService shiftService;
    @Override
    public void run(String... args){
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(25.3)
                .build();

        employee = employeeRepository.save(employee);

        Employee finalEmployee = employee;
        Stream<Shift> randomShifts = Stream.generate(() -> {
            String startTime = randomTime();
            String endTime = randomTime();
            return Shift.builder()
                    .station("Station " + (new Random().nextInt(90) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime(startTime)
                    .endTime(endTime)
                    .actualStartTime("00:00")
                    .actualEndTime("14:24")
                    .employee(finalEmployee)
                    .build();
        }).limit(10);

        shiftRepository.saveAll(randomShifts.collect(Collectors.toList()));

        System.out.println(employeeRepository.getEmployeeById(1L));

        System.out.println(employeeService.getMonthSalary(1L,5));
        System.out.println(employeeService.getMonthTax(1L,5));
        System.out.println(employeeService.getMonthRevenue(1L,5));
    }
    private String randomTime() {
        int hour = new Random().nextInt(24);
        int minute = new Random().nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }
}
