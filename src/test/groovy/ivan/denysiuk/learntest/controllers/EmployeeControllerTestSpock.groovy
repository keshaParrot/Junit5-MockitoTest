package ivan.denysiuk.learntest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import ivan.denysiuk.learntest.services.interfaces.EmployeeService
import ivan.denysiuk.learntest.domains.dtos.EmployeeDto
import ivan.denysiuk.learntest.domains.entities.Employee
import ivan.denysiuk.learntest.domains.entities.Shift
import ivan.denysiuk.learntest.domains.entities.TypeOfContract
import ivan.denysiuk.learntest.domains.mappers.EmployeeMapper
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate
import java.time.YearMonth
import java.util.stream.Collectors
import java.util.stream.Stream

import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EmployeeControllerTestSpock extends Specification {

    def employeeService = Mock(EmployeeService)
    def employeeController = new EmployeeController(employeeService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build()

    @Shared Employee employee
    @Shared EmployeeDto employeeDto
    @Shared List<Employee> listOfEmployees = new ArrayList<>()
    @Shared ObjectMapper objectMapper = new ObjectMapper()
    String EMPLOYEE_PATH = employeeController.EMPLOYEE_PATH

    void setupSpec() {
        Stream<Shift> randomShifts = Stream.generate(() -> {
            return Shift.builder()
                    .station("Station " + (new Random().nextInt(10) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime("00:00")
                    .endTime("14:24")
                    .actualStartTime("00:00")
                    .actualEndTime("14:24")
                    .employee(null)
                    .build()
        }).limit(5)

        employee = Employee.builder()
                .id(1L)
                .firstName("Iga")
                .lastName("Kowalska")
                .rate(25.0)
                .typeOfContract(TypeOfContract.PRACA)
                .specialization("Developer")
                .dateOfBirthday(LocalDate.of(1990, 5, 3))
                .PESEL("12345678901")
                .ZUSindex("123456789")
                .workedShift(randomShifts.collect(Collectors.toUnmodifiableSet()))
                .build()

        employeeDto = getDtoFromEmployee(employee)
        listOfEmployees.add(employee)
        objectMapper.registerModule(new JavaTimeModule())
    }
    def "GetAllEmployee"() {
        given:
        employeeService.getAllEmployees(pageNumber, pageSize) >> new PageImpl<>(allEmployee)

        when:
            def result = mockMvc.perform(get(EMPLOYEE_PATH)
                    .param("numberofpage", pageNumber as String)
                    .param("pagesize", pageSize as String)
                    .accept(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status)

        where:
            allEmployee     | status                  | pageNumber | pageSize
            listOfEmployees | status().isOk()         | 5          | 5
            new ArrayList() | status().isNotFound()   | 6          | 6
            new ArrayList() | status().isBadRequest() | null       | null
    }

    def "GetEmployeeById"() {
        given:
            employeeService.getEmployeeById(employeeDto.getId()) >> employee

        when:
            def result = mockMvc.perform(get(EMPLOYEE_PATH+"/${employeeDto.getId()}")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employeeDto)))

        then:
            result.andExpect(status().isOk())
                    .andExpect(jsonPath('$.id', is(employeeDto.getId().intValue())))
                    .andExpect(jsonPath('$.rate', is(employeeDto.getRate().doubleValue())))
    }

    def "getEmployeeBy"() {
        given:
            employeeService.findByParams(pesel, fullname, specialization) >> entity

        when:
            def requestBuilder = get(EMPLOYEE_PATH + "/searchby")
            if (pesel != null) {
                requestBuilder.param("pesel", pesel as String)
            }
            if (fullname != null) {
                requestBuilder.param("fullname", fullname as String)
            }
            if (specialization != null) {
                requestBuilder.param("specialization", specialization as String)
            }
            def result = mockMvc.perform(requestBuilder.accept(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status)

        where:
            pesel | fullname | specialization | status                  | entity
            null  | null     | null           | status().isBadRequest() | new ArrayList<>()
            null  | null     | "Developer"    | status().isOk()         | listOfEmployees
            null  | "A B"    | null           | status().isNotFound()   | new ArrayList<>()

    }

    def "DeleteEmployee"() {
        when:
            def result = mockMvc.perform(delete(EMPLOYEE_PATH+"/${empId}/delete"))

        then:
            result.andExpect(status)
            1 * employeeService.deleteEmployee(empId) >> bool

        where:
            empId | status                | bool
            1     | status().isOk()       | true
            2     | status().isNotFound() | false

    }

    def "GetEmployeeSalary"() {
        given:
            employeeService.getEmployeeById(empId) >> Entity
            employeeService.getMonthSalary(empId,month) >> salary
            employeeService.getMonthTax(empId,month) >> tax

        when:
            def result = mockMvc.perform(get(EMPLOYEE_PATH + "/${empId}/salary")
                    .param("month", month as String)
                    .contentType(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status)

        where:
            month | empId | status                  | Entity         | salary | tax
            5     | 1L    | status().isNotFound()   | new Employee() | 0.0    | new HashMap<>()
            5     | 2L    | status().isNotFound()   | null           | 0.0    | new HashMap<>()
            5     | 3L    | status().isOk()         | employee       | 400.0  | [Tax: 500.0 as Double]
            6     | 3L    | status().isNotFound()   | employee       | 0.0    | new HashMap<>()

    }

    def "SaveEmployee"() {
        given:
            employeeService.saveEmployee(_ as EmployeeDto) >> Entity

        when:
            def result = mockMvc.perform(post(EMPLOYEE_PATH + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Dto)))

        then:
            assert checkHeaderErrorsExists(result) == errors
            result.andExpect(status)
                    .andExpect(jsonPath('$.id', is(Dto.id  as Integer)))
                    .andExpect(jsonPath('$.firstName', is(Dto.firstName)))
                    .andExpect(jsonPath('$.rate',is(Dto.rate  as Double)))

        where:
            Dto               | Entity                 | status                  | errors
            employeeDto       | listOfEmployees.get(0) | status().isCreated()    | false
            new EmployeeDto() | new Employee()         | status().isBadRequest() | true
    }

    def "UpdateEmployee"() {
        given:
            employeeService.updateEmployee(empId,_ as EmployeeDto) >> Entity

        when:
            def result = mockMvc.perform(put(EMPLOYEE_PATH +"/${empId}/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Dto)))

        then:
            assert checkHeaderErrorsExists(result) == errors
            result.andExpect(status)
                    .andExpect(jsonPath('$.id', is(Dto.id  as Integer)))
                    .andExpect(jsonPath('$.firstName', is(Dto.firstName)))
                    .andExpect(jsonPath('$.rate',is(Dto.rate  as Double)))

        where:
            empId | Dto               | Entity                 | status                  | errors
            1L    | employeeDto       | listOfEmployees.get(0) | status().isAccepted()   | false
            2L    | new EmployeeDto() | new Employee()         | status().isBadRequest() | true
    }

    def "PatchEmployee"() {
        given:
            employeeService.patchEmployee(empId,_ as EmployeeDto) >> Entity

        when:
            def result = mockMvc.perform(patch(EMPLOYEE_PATH +"/${empId}/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Dto)))

        then:
            result.andExpect(status)

        where:
            empId | Dto               | Entity                 | status
            1L    | employeeDto       | listOfEmployees.get(0) | status().isAccepted()
            2L    | new EmployeeDto() | null                   | status().isNotFound()
    }
    boolean checkHeaderErrorsExists(ResultActions actions) {
        try {
            actions.andExpect(MockMvcResultMatchers.header().exists("Errors"))
            return true
        } catch (AssertionError e) {
            return false
        }
    }
    private EmployeeDto getDtoFromEmployee(Employee employee){
        return EmployeeMapper.INSTANCE.employeeToDto(employee);
    }
}
