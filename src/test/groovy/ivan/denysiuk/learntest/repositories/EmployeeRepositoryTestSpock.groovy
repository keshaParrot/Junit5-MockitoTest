package ivan.denysiuk.learntest.repositories

import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@TestPropertySource(locations = "classpath:application-test.properties")
class EmployeeRepositoryTestSpock extends Specification {


    def "GetEmployeeById"() {

    }

    def "GetEmployeeByFullName"() {

    }

    def "GetEmployeeByPESEL"() {

    }
}
