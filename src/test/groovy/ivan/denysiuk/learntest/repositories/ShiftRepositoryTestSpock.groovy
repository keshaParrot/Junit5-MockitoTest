package ivan.denysiuk.learntest.repositories


import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@TestPropertySource(locations = "classpath:application-test.properties")
class ShiftRepositoryTestSpock extends Specification {

    /*@Subject
    @Autowired
    ShiftRepository shiftRepository
    def shift

    def setup() {
        shift = Shift.builder()
                .id(1L)
                .date(LocalDate.of(2022,2,2))
                .startTime("14:40")
                .build()
    }

    def "GetShiftById"() {
        given:
            shiftRepository.save(shift)
        when:
            def result = shiftRepository.getShiftById(1L)
        then:
            result == expe
    }

    def "GetShiftsByEmployeeId"() {
    }*/
}
