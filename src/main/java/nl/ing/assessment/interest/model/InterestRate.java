package nl.ing.assessment.interest.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record InterestRate(Integer maturityPeriod, BigDecimal interestRate, Timestamp lastUpdate) {
}
