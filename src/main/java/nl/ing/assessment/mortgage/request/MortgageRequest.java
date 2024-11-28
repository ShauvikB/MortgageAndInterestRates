package nl.ing.assessment.mortgage.request;

import java.math.BigDecimal;

public record MortgageRequest(BigDecimal income, Integer maturityPeriod, BigDecimal loanValue, BigDecimal homeValue) {
}
