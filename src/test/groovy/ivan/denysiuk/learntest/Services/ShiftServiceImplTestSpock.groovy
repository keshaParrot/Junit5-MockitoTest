package ivan.denysiuk.learntest.Services

import ivan.denysiuk.learntest.Repository.EmployeeRepository
import ivan.denysiuk.learntest.Repository.ShiftRepository
import ivan.denysiuk.learntest.domain.dto.ShiftDto
import ivan.denysiuk.learntest.domain.entity.Employee
import ivan.denysiuk.learntest.domain.entity.Shift
import ivan.denysiuk.learntest.domain.mapper.ShiftMapper
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
                .date(LocalDate.of(2022,9,12))
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
        when: "when employee does not exist"
            employeeRepository.existsById(1L) >> false
            List<Shift> resultWhenEmployeeDoesNotExist = shiftService.getAllShiftByEmployeeFromToDate(1L, YearMonth.of(2023, 3), YearMonth.of(2023, 9))
        then:
            resultWhenEmployeeDoesNotExist.isEmpty()

        when: "when employee exist, shift does not exist"
            employeeRepository.existsById(1L) >> true
            shiftRepository.getShiftsByEmployeeId(1L) >> []
            List<Shift> resultWhenNoShifts = shiftService.getAllShiftByEmployeeFromToDate(1L, YearMonth.of(2023, 3), YearMonth.of(2023, 9))
        then:
            resultWhenNoShifts.isEmpty()

        when: "when employee exist, shift exist"
            shiftRepository.getShiftsByEmployeeId(1L) >> listOfShifts
            List<Shift> resultWithShiftsInRange = shiftService.getAllShiftByEmployeeFromToDate(1L, YearMonth.of(2023, 3), YearMonth.of(2023, 9))
        then:
            resultWithShiftsInRange.size() == 1
            resultWithShiftsInRange[0].date == LocalDate.of(2023, 2, 15)
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
            boolean resultOfDeleting = shiftService.deleteShift(shiftId);
        then:
            resultOfDeleting == result
        where:
            shiftId | result
            1L      | true
            2L      | false
    }

    def "patchEmployee"() { //TODO maybe we need to change method name to "test patch", and change logic to patch all"
        given:
            ShiftDto shiftToUpdate = new ShiftDto()
            Shift shift = listOfShifts.get(0)
            Employee employee = new Employee()

            shiftToUpdate.setEmployeeId(args)
            shiftRepository.getShiftById(1L) >> shift
            employeeRepository.getEmployeeById(1L) >> employee
        when:

            Shift result = shiftService.patchShift(1L,shiftToUpdate)

        then:
            result.getEmployee() == expectedArgs

        where:
            args    | expectedArgs
            1L      | employee
            5L      | null
    }

    def "CountAllShift"() {

    }

    Shift getShiftFromDto(ShiftDto shift){
        return ShiftMapper.INSTANCE.DtoToShift(shift)
    }
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift)
    }
}
