package nl.ing.assessment.service;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.interest.model.InterestRate;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import nl.ing.assessment.mortgage.response.MortgageResponse;
import nl.ing.assessment.util.MortgageAndInterestRatesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MortgageAndInterestRatesService {

    @Autowired
    private MortgageAndInterestRatesUtil mortgageAndInterestRatesUtil;

    /**
     * Get all interest rates
     *
     * @return List of interest rates
     */
    public List<InterestRate> getAllInterestRates() {
        return mortgageAndInterestRatesUtil.getInterestRates();
    }


    public MortgageResponse checkMortgage(MortgageRequest request)  {
        log.info("Checking mortgage feasibility for request: {}", request);
        // a mortgage should not exceed 4 times the income and the loan value should not exceed the home value
        BigDecimal maxLoanValue = request.income().multiply(BigDecimal.valueOf(4));
        log.info("Max loan value: {}", maxLoanValue);
        boolean feasible = request.loanValue().compareTo(maxLoanValue) <= 0 &&
                request.loanValue().compareTo(request.homeValue()) <= 0;

        BigDecimal monthlyCost = calculateMonthlyCost(request.loanValue(), request.maturityPeriod());

        MortgageResponse response = new MortgageResponse(feasible, monthlyCost);
        log.info("Mortgage feasibility check result: {}", response);
        return response;
    }

    /**
     * Calculate the monthly cost of a loan
     *
     * @param loanValue      the loan value
     * @param maturityPeriod the maturity period in years
     *
     * @return the monthly cost
     */
    private BigDecimal calculateMonthlyCost(BigDecimal loanValue, int maturityPeriod)  {
        log.info("Calculating monthly cost for loan value: {} and maturity period: {}", loanValue, maturityPeriod);
        BigDecimal interestRate = null;
        try {
            interestRate = mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(maturityPeriod);
            log.info("Interest rate for maturity period: {} is: {}", maturityPeriod, interestRate);
        } catch (MortgageAndInterestRatesException e) {
            log.error("Error getting interest rate for maturity period: {}", maturityPeriod, e);
            return BigDecimal.ZERO;
        }

       return null;
    }
}
