package ivan.denysiuk.learntest.Repository

import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@TestPropertySource(locations = "classpath:application-test.properties")
class EmployeeRepositoryTestSpock extends Specification {
    def employeeRepository = spy(EmployeeRepository)

    def "GetEmployeeById"() {

    }

    def "GetEmployeeByFullName"() {

    }

    def "GetEmployeeByPESEL"() {

    }
}
