package ivan.denysiuk.learntest.domains.dtos;

import java.util.Map;

public record SalaryDto(Long employeeId, String fullName, Map<String, Double> Tax, double salary) {
}
