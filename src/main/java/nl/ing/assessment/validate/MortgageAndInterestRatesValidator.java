package nl.ing.assessment.validate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@NoArgsConstructor
public class MortgageAndInterestRatesValidator {

    /**
     * Validate mortgage details
     *
     * @param request      Mortgage request
     *
     * @throws MortgageAndInterestRatesException
     */
    public void validateMortgageDetails(MortgageRequest request) throws MortgageAndInterestRatesException {
        log.info("Validating mortgage details for request: {} ", request);

        BigDecimal maxLoanValue = request.income().multiply(BigDecimal.valueOf(4));
        log.info("Max loan value: {}", maxLoanValue);

        if (maxLoanValue.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Income is less than or equal to zero");
            throw new MortgageAndInterestRatesException("An income must be greater than zero");
        }
        if (request.homeValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Home value is less than or equal to zero");
            throw new MortgageAndInterestRatesException("A home value must be greater than zero");
        }
        if (request.maturityPeriod() <= 0) {
            log.info("Maturity period is less than or equal to zero");
            throw new MortgageAndInterestRatesException("A maturity period must be greater than zero");
        }

        if (request.loanValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Loan value is less than or equal to zero");
            throw new MortgageAndInterestRatesException("A loan value must be greater than zero");
        }
        else if (request.loanValue().compareTo(maxLoanValue) > 0 ) {
            log.info("Loan value is more than 4 times the income");
            throw new MortgageAndInterestRatesException("A Mortgage cannot be more than 4 times the income");
        } else if (request.loanValue().compareTo(request.homeValue()) > 0) {
            log.info("Loan value is more than the home value");
            throw new MortgageAndInterestRatesException("A Mortgage cannot be more than the home value");
        }
    }
}
