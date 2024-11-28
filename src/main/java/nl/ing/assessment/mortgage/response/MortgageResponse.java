package nl.ing.assessment.mortgage.response;

import java.math.BigDecimal;

public record MortgageResponse(boolean feasible, BigDecimal monthlyCost) {
}
