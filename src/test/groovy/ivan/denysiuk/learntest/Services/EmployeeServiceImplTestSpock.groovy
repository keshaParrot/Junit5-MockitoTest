package ivan.denysiuk.learntest.Services

import ivan.denysiuk.learntest.Repository.EmployeeRepository
import ivan.denysiuk.learntest.domain.dto.EmployeeDto
import ivan.denysiuk.learntest.domain.entity.Employee
import ivan.denysiuk.learntest.domain.entity.Shift
import ivan.denysiuk.learntest.domain.mapper.EmployeeMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.util.stream.Collectors
import java.util.stream.Stream

class EmployeeServiceImplTestSpock extends Specification {

    def employeeRepository = Mock(EmployeeRepository)
    @Subject
    def employeeService = new EmployeeServiceImpl(employeeRepository)

    @Shared EmployeeDto employeeDto
    @Shared List<Employee> listOfEmployee = []

    def setupSpec() {
        Stream<Shift> randomShifts = Stream.generate(() -> {
            String startTime = randomTime()
            String endTime = randomTime()
            return Shift.builder()
                    .station("Station " + (new Random().nextInt(10) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime(startTime)
                    .endTime(endTime)
                    .actualStartTime("10:00")
                    .actualEndTime("20:00")
                    .employee(null)
                    .build()
        }).limit(5)



        def employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .workedShift(randomShifts.collect(Collectors.toUnmodifiableSet()))
                .build()

        employeeDto = EmployeeDto.builder()
                .id(2L)
                .firstName("Iga")
                .lastName("Nowak")
                .PESEL("03947282343")
                .rate(23.5)
                .build()

        listOfEmployee << employee
        listOfEmployee << getEmployeeFromDto(employeeDto)
    }
    String randomTime() {
        int hour = new Random().nextInt(24)
        int minute = new Random().nextInt(60)
        return String.format("%02d:%02d", hour, minute)
    }
    def "GetAllEmployees"() {
        when:
            employeeRepository.findAll() >> listOfEmployee
            def result = employeeService.getAllEmployees()
        then:
            result == listOfEmployee
    }

    def "GetEmployeeById"() {
        when:
            employeeRepository.getEmployeeById(1L) >> listOfEmployee.get(0)
            employeeRepository.getEmployeeById(2L) >> listOfEmployee.get(1)
            def result = employeeService.getEmployeeById(i)
        then:
            result == expect
        where:
            i  | expect
            1L | listOfEmployee.get(0)
            2L | listOfEmployee.get(1)
            3L | null
    }

    def "GetEmployeeByPESEL"() {
        when:
            employeeRepository.getEmployeeByPESEL("03947283728") >> listOfEmployee.get(0)
            employeeRepository.getEmployeeByPESEL("03947282343") >> listOfEmployee.get(1)
            def result = employeeService.getEmployeeByPESEL(i)
        then:
            result == expect
        where:
            i             | expect
            "03947283728" | listOfEmployee.get(0)
            "03947282343" | listOfEmployee.get(1)
            "47358745935" | null
    }

    def "GetEmployeeByFullName"() {
        when:
            employeeRepository.getEmployeeByFullName("Adam Kowalski") >> listOfEmployee.get(0)
            employeeRepository.getEmployeeByFullName("Iga Nowak") >> listOfEmployee.get(1)
            def result = employeeService.getEmployeeByfullName(i)
        then:
            result == expect
        where:
            i              | expect
            "Adam Kowalski"| listOfEmployee.get(0)
            "Iga Nowak"    | listOfEmployee.get(1)
            "Adam Nowak"   | null
    }

    def "AddEmployee"() {
        when:
            employeeService.saveEmployee(employeeDto)
        then:
            1 * employeeRepository.save(getEmployeeFromDto(employeeDto))
    }

    def "UpdateEmployee"() {
        when:
            employeeService.updateEmployee(2L,employeeDto)
        then:
            1 * employeeRepository.save(getEmployeeFromDto(employeeDto))
    }
    def "PatchEmployee"() {
        given:
            def employee = listOfEmployee.get(0)
        when:
            employeeRepository.getEmployeeById(1L) >> employee
            employeeService.patchEmployee(1L,employeeDto)
        then:
            1 * employeeRepository.save(employee)
    }
    def "DeleteEmployee"() {
        given:
            def employee = getEmployeeFromDto(employeeDto)
        when:
            employeeRepository.existsById(1L) >> true
            employeeService.deleteEmployee(1L)
        then:
            1 * employeeRepository.deleteById(1L)
    }

    def "CountAllEmployees"() {
        given:
            def actualNumberOfEmployee = 2
        when:
            employeeRepository.count() >> actualNumberOfEmployee
        then:
            def result = employeeService.countAllEmployees()
        expect:
            result == actualNumberOfEmployee
    }

    def "GetMonthSalary"() {
        given:
            def employee1 = listOfEmployee.get(0)
            def employee2 = listOfEmployee.get(1)
        when:
            employeeRepository.getEmployeeById(1L)>>employee1
            employeeRepository.getEmployeeById(2L)>>employee2
            Double salary = employeeService.getMonthSalary(employeeId,5)
        then:
            salary == result
        where:
            employeeId  | result
            1L          | 1175.0
            2L          | 0.0
    }

    def "GetMonthTax"() {



    }

    def "GetMonthRevenue"() {
        given:
            def employee1 = listOfEmployee.get(0)
            def employee2 = listOfEmployee.get(1)
        when:
            employeeRepository.getEmployeeById(1L)>>employee1
            employeeRepository.getEmployeeById(2L)>>employee2
            Double salary = employeeService.getMonthRevenue(employeeId,5)
        then:
            salary == result
        where:
            employeeId  | result
            1L          | 1175.0
            2L          | 0.0
    }

    Employee getEmployeeFromDto(EmployeeDto employee){
        return EmployeeMapper.INSTANCE.dtoToEmployee(employee);
    }
    EmployeeDto getDtoFromEmployee(Employee employee){
        return EmployeeMapper.INSTANCE.employeeToDto(employee);
    }
}
