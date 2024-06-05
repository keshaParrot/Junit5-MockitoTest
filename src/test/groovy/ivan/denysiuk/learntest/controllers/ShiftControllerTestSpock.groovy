package ivan.denysiuk.learntest.controllers

import ivan.denysiuk.learntest.services.interfaces.EmployeeService
import ivan.denysiuk.learntest.validators.CustomConstraintValidatorFactory
import ivan.denysiuk.learntest.domains.mappers.ShiftMapper
import jakarta.validation.Validation
import jakarta.validation.ValidatorFactory
import jakarta.validation.Validator
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import ivan.denysiuk.learntest.services.interfaces.ShiftService
import ivan.denysiuk.learntest.validators.EmployeeExistValidator
import ivan.denysiuk.learntest.domains.dtos.ShiftDto
import ivan.denysiuk.learntest.domains.entities.Employee
import ivan.denysiuk.learntest.domains.entities.Shift
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.hibernate.validator.HibernateValidator
import org.springframework.http.MediaType
import spock.lang.Shared
import spock.lang.Specification
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.time.LocalDate

import static java.time.YearMonth.*
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ShiftControllerTestSpock extends Specification {

    def shiftService = Mock(ShiftService)
    def employeeService = Mock(EmployeeService)
    def shiftController = new ShiftController(shiftService)
    MockMvc mockMvc

    @Shared Shift shift
    @Shared ShiftDto shiftDto
    @Shared def listOfShift = []
    @Shared ObjectMapper objectMapper = new ObjectMapper()
    String SHIFT_PATH = shiftController.SHIFT_PATH

    void setupSpec() {
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Ploch")
                .build()

        shift = Shift.builder()
                .id(1L)
                .station("Station A")
                .startTime("22:30")
                .endTime("06:00")
                .date(LocalDate.of(2024, 5, 4))
                .employee(employee)
                .build()

        shiftDto = getDtoFromShift(shift)
        listOfShift << shift
        objectMapper.registerModule(new JavaTimeModule())
    }

    def initManualDtoValidator() {
        EmployeeExistValidator employeeExistValidator = new EmployeeExistValidator(employeeService)

        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .constraintValidatorFactory(new CustomConstraintValidatorFactory(employeeExistValidator))
                .buildValidatorFactory()

        Validator validator = validatorFactory.getValidator()

        mockMvc = MockMvcBuilders.standaloneSetup(shiftController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build()
    }

    def "GetShiftById"() {
        given:
            initManualDtoValidator()
            shiftService.getShiftById(shift.getId()) >> shift

        when:
            def result = mockMvc.perform(get(SHIFT_PATH+"/${shift.getId()}")
                    .accept(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status().isOk())
                    .andExpect(jsonPath('$.id', is(shift.getId().intValue())))
                    .andExpect(jsonPath('$.startTime', is(shift.getStartTime())))
    }


    def "AddShiftsToDatabase"() {
        given:
            initManualDtoValidator()
            employeeService.existById(_ as Long) >> empExist
            shiftService.saveShift(_ as ShiftDto) >> shiftObject

        when:
            def result = mockMvc.perform(post(SHIFT_PATH + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(DtoObject)))

        then:
            assert checkHeaderErrorsExists(result) == errors
            result.andExpect(status)
                    .andExpect(jsonPath('$.id', is(id)))
                    .andExpect(jsonPath('$.employeeId', is(employeeId)))

        where:
            DtoObject       | shiftObject  | status                  | errors | id    | employeeId | empExist
            shiftDto        | shift        | status().isCreated()    | false  | 1     | 1          | true
            new ShiftDto()  | new Shift()  | status().isBadRequest() | true   | null  | null       | false
    }
    def "GetAllShiftByEmployee"() {
        given:
            initManualDtoValidator()
            shiftService.getAllShiftByEmployee(empId) >> shifts

        when:
            def result = mockMvc.perform(get(SHIFT_PATH)
                    .param("emp", empId as String)
                    .contentType(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status)
            assert checkHeaderErrorsExists(result) == errors
            if(shifts.size()>0){
                result.andExpect(jsonPath('$[0].employeeId', is(empId as Integer)))
                        .andExpect(jsonPath('$[0].startTime', is(startTime)))
            }
        where:
            empId | status                  | shifts            | startTime | errors
            1L    | status().isOk()         | listOfShift       | "22:30"   | false
            2L    | status().isNotFound()   | new ArrayList<>() | null      | true
            null  | status().isBadRequest() | new ArrayList<>() | null      | true

    }
    def "GetAllShiftByEmployeeFromToDate"() {
        given:
            initManualDtoValidator()
            shiftService.getAllShiftByEmployeeFromToDate(empId,from,to) >> shifts

        when:
        def result = mockMvc.perform(get(SHIFT_PATH)
                .param("emp", empId as String)
                .param("from", from as String)
                .param("to", to as String)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status)
        assert checkHeaderErrorsExists(result) == errors
        if(shifts.size()>0){
            result.andExpect(jsonPath('$[0].employeeId', is(empId as Integer)))
                    .andExpect(jsonPath('$[0].startTime', is(startTime)))
        }
        where:
            empId| from       | to         | status                  | shifts            | startTime | errors
            1L   | of(2022,3) | of(2022,9) | status().isOk()         | listOfShift       | "22:30"   | false
            2L   | of(2022,6) | of(2022,9) | status().isNotFound()   | new ArrayList<>() | null      | true
            3L   | of(2022,3) | of(2022,9) | status().isNotFound()   | new ArrayList<>() | null      | true
            null | of(2022,3) | of(2022,9) | status().isBadRequest() | new ArrayList<>() | null      | true
    }
    def "DeleteShiftFromDatabase"() {
        given:
            initManualDtoValidator()

        when:
            def result = mockMvc.perform(delete(SHIFT_PATH+"/${shiftId}/delete")
                    .accept(MediaType.APPLICATION_JSON))

        then:
            result.andExpect(status)
            1 * shiftService.deleteShift(shiftId) >> bool

        where:
            shiftId | status                | bool
            1       | status().isOk()       | true
            2       | status().isNotFound() | false
    }
    def "UpdateShift"() {
        given:
            initManualDtoValidator()
            employeeService.existById(_ as Long) >> true
            shiftService.updateShift(shiftId,dtoObject) >> shiftObject

        when:
            def result = mockMvc.perform(put(SHIFT_PATH+"/${shiftId}/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoObject)))

        then:
            assert checkHeaderErrorsExists(result) == errors
            result.andExpect(status)

        where:
            shiftId | dtoObject      | shiftObject | status                  | errors
            1L      | shiftDto       | shift       | status().isOk()         | false
            2L      | new ShiftDto() | new Shift() | status().isBadRequest() | true
            99L     | shiftDto       | null        | status().isNotFound()   | true


    }
    def "PatchShift"() {
        given:
            initManualDtoValidator()
            employeeService.existById(_ as Long) >> true
            shiftService.patchShift(shiftId,dtoObject) >> shiftObject

        when:
            def result = mockMvc.perform(patch(SHIFT_PATH+"/${shiftId}/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoObject)))

        then:
            assert checkHeaderErrorsExists(result) == errors
            result.andExpect(status)

        where:
            shiftId | dtoObject      | shiftObject | status                  | errors
            1L      | shiftDto       | shift       | status().isOk()         | false
            2L      | new ShiftDto() | new Shift() | status().isBadRequest() | true
            99L     | shiftDto       | null        | status().isNotFound()   | true
    }

    boolean checkHeaderErrorsExists(ResultActions actions) {
        try {
            actions.andExpect(MockMvcResultMatchers.header().exists("Errors"))
            return true
        } catch (AssertionError e) {
            return false
        }
    }
    Shift getShiftFromDto(ShiftDto shiftDto){
        return ShiftMapper.INSTANCE.DtoToShift(shiftDto)
    }
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift)
    }
}
