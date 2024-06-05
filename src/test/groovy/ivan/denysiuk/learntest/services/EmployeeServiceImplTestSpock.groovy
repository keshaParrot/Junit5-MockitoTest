package ivan.denysiuk.learntest.services

import ivan.denysiuk.learntest.repositories.EmployeeRepository
import ivan.denysiuk.learntest.domains.dtos.EmployeeDto
import ivan.denysiuk.learntest.domains.entities.Employee
import ivan.denysiuk.learntest.domains.entities.Shift
import ivan.denysiuk.learntest.domains.mappers.EmployeeMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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
                    .station("Station " + (new Random().nextInt(99) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime(startTime)
                    .endTime(endTime)
                    .actualStartTime("00:00")
                    .actualEndTime("14:24")
                    .employee(null)
                    .build()
        }).limit(10)

        def employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(25.3)
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
        given:
            Page<Employee> mockedPage = new PageImpl<>(listOfEmployee)

        when:
            employeeRepository.findAll(PageRequest.of(0, 1)) >> mockedPage
            Page<Employee> pagedEmployee = employeeService.getAllEmployees(0,1)
            List<Employee> allEmployee = pagedEmployee.getContent()
        then:
            allEmployee == listOfEmployee

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


    def "findByPESEL"() {
        when:
            employeeRepository.findByParams(empPesel,null,null) >> employee
            def result = employeeService.findByParams(empPesel,null,null)
        then:
            def answer = result.isEmpty()? true : result.get(0) == expect
            answer == true
        where:
            employee                       | empPesel      | expect
            List.of(listOfEmployee.get(0)) | "03947283728" | listOfEmployee.get(0)
            List.of(listOfEmployee.get(1)) | "03947282343" | listOfEmployee.get(1)
            Collections.emptyList()        | "47358745935" | null
    }

    def "findByFullName"() {
        when:
            employeeRepository.findByParams(null,empFullName,null) >> employee
            def result = employeeService.findByParams(null,empFullName,null)
        then:
            def answer = result.isEmpty()? true : result.get(0) == expect
            answer == true
        where:
            employee                       | empFullName     | expect
            List.of(listOfEmployee.get(0)) | "Adam Kowalski" | listOfEmployee.get(0)
            List.of(listOfEmployee.get(1)) | "Iga Nowak"     | listOfEmployee.get(1)
            Collections.emptyList()        | "Adam Nowak"    | null
    }

    def "findBySpecialization"() {
        when:
            employeeRepository.findByParams(null,null,empSpecialization) >> employee
            def result = employeeService.findByParams(null,null,empSpecialization)
        then:
            def answer = result.isEmpty()? true : result.get(0) == expect
            answer == true
        where:
            employee                       | empSpecialization | expect
            List.of(listOfEmployee.get(0)) | "Developer"       | listOfEmployee.get(0)
            List.of(listOfEmployee.get(1)) | "Designer"        | listOfEmployee.get(1)
            Collections.emptyList()        | "Teacher"         | null
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
            1L          | 3643.2
            2L          | 0.0
    }

    def "GetMonthTax"() { //TODO finish this method



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
            1L          | 2380.16
            2L          | 0.0
    }

    Employee getEmployeeFromDto(EmployeeDto employee){
        return EmployeeMapper.INSTANCE.dtoToEmployee(employee)
    }
}
