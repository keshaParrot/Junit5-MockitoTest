package ivan.denysiuk.learntest.services

import ivan.denysiuk.learntest.repositories.EmployeeRepository
import ivan.denysiuk.learntest.repositories.ShiftRepository
import ivan.denysiuk.learntest.domains.dtos.ShiftDto
import ivan.denysiuk.learntest.domains.entities.Employee
import ivan.denysiuk.learntest.domains.entities.Shift
import ivan.denysiuk.learntest.domains.mappers.ShiftMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.YearMonth

class ShiftServiceImplTestSpock extends Specification {

    def shiftRepository = Mock(ShiftRepository)
    def employeeRepository = Mock(EmployeeRepository)
    @Subject def shiftService = new ShiftServiceImpl(shiftRepository,employeeRepository)

    @Shared List<Shift> listOfShifts = []
    @Shared ShiftDto shiftDto

    def setupSpec(){
        def employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .build()

        def shift = Shift.builder()
                .id(1L)
                .date(LocalDate.of(2022,2,12))
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .employee(employee)
                .build()

        shiftDto = ShiftDto.builder()
                .id(2L)
                .date(LocalDate.of(2022,8,12))
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .employeeId(1L)
                .build()

        listOfShifts << shift
        listOfShifts << getShiftFromDto(shiftDto)
    }
    def "GetShiftById"() {
        when:
            shiftRepository.getShiftById(1L) >> listOfShifts.get(0)
            shiftRepository.getShiftById(2L) >> listOfShifts.get(1)
            def result = shiftService.getShiftById(i)
        then:
            result == expect
        where:
            i  | expect
            1L | listOfShifts.get(0)
            2L | listOfShifts.get(1)
            3L | null
    }

    def "GetAllShiftByEmployee"() {
        when:
            shiftRepository.getShiftsByEmployeeId(1L) >> listOfShifts
            employeeRepository.existsById(1L) >> true
            employeeRepository.existsById(2L) >> false
            def result = shiftService.getAllShiftByEmployee(i)
        then:
            result == expect
        where:
            i  | expect
            1L | listOfShifts
            2L | Collections.emptyList()
    }

    def "GetAllShiftByEmployeeFromToDate"() {
        when: "employee does not exist"
        employeeRepository.existsById(1L) >> false

        and:
        List<Shift> resultWhenEmployeeDoesNotExist = shiftService.getAllShiftByEmployeeFromToDate(1L, YearMonth.of(2022, 3), YearMonth.of(2022, 9))

        then:
        resultWhenEmployeeDoesNotExist.isEmpty()

        when: "employee exists but shifts do not exist"
        employeeRepository.existsById(2L) >> true
        shiftRepository.getShiftsByEmployeeId(1L) >> []

        and:
        List<Shift> resultWhenNoShifts = shiftService.getAllShiftByEmployeeFromToDate(2L, YearMonth.of(2023, 3), YearMonth.of(2023, 6))

        then:
        resultWhenNoShifts.isEmpty()

        when: "employee exists and shifts exist"
        employeeRepository.existsById(3L) >> true
        shiftRepository.getShiftsByEmployeeId(3L) >> listOfShifts

        and:
        List<Shift> resultWithShiftsInRange = shiftService.getAllShiftByEmployeeFromToDate(3L, YearMonth.of(2022, 3), YearMonth.of(2022, 9))

        then:
        resultWithShiftsInRange.size() == 1
        resultWithShiftsInRange[0].date == LocalDate.of(2022, 8, 12)
    }

    def "AddShiftToDatabase"() {
        given:
            Employee employee = Employee.builder()
                    .id(1L)
                    .firstName("Jan")
                    .workedShift(new HashSet<Shift>())
                    .build()

            Shift shift = listOfShifts.get(1)

        when:
            def savedShift = shiftService.saveShift(shiftDto)

        then:
            1 * employeeRepository.getEmployeeById(1L) >> employee
            1 * shiftRepository.save(shift) >> shift
            savedShift == shift
    }

    def "DeleteShiftFromDatabase"() {
        given:
        shiftRepository.existsById(_ as Long) >> { Long id -> id == 1L }
        when:
            boolean resultOfDeleting = shiftService.deleteShift(shiftId)
        then:
            resultOfDeleting == result
        where:
            shiftId | result
            1L      | true
            2L      | false
    }

    def "patchEmployee"() {
        given:
            Shift shift = listOfShifts.get(0)
            ShiftDto shiftToUpdate = ShiftDto.builder()
                    .employeeId(1L)
                    .startTime("13:45")
                    .endTime("19:45")
                    .build()
            Employee employee = Employee.builder()
                    .id(1L)
                    .firstName("Lora")
                    .lastName("Mroz")
                    .build()

        when:
            Shift result = shiftService.patchShift(1L, shiftToUpdate)

        then:
            result.getEmployee() == employee
            result.getStartTime() == "13:45"
            result.getEndTime() == "19:45"

            1 * shiftRepository.getShiftById(1L) >> shift
            1 * employeeRepository.getEmployeeById(1L) >> employee
            1 * shiftRepository.save(_ as Shift) >> { Shift s -> s }
    }

    def "CountAllShift"() {
        given:
            shiftRepository.count() >> 5
        when:
            int result = shiftService.countAllShift()
        then:
            result == 5
    }

    Shift getShiftFromDto(ShiftDto shift){
        return ShiftMapper.INSTANCE.DtoToShift(shift)
    }
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift)
    }
}
