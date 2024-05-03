package ivan.denysiuk.learntest.domain.dto;

import ivan.denysiuk.learntest.domain.entity.Employee;

import java.util.Map;

public record SalaryDto(Long employeeId, String fullName, Map<String, Double> Tax, double salary) {
}
