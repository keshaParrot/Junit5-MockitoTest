package ivan.denysiuk.learntest.Repository

import ivan.denysiuk.learntest.domain.entity.Shift
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

@TestPropertySource(locations = "classpath:application-test.properties")
class ShiftRepositoryTestSpock extends Specification {

    /*@Subject
    def shiftRepository = mock(ShiftRepository)
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
