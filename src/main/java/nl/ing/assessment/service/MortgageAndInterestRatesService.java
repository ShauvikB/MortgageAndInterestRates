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
import java.math.RoundingMode;
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
        if (request.loanValue().compareTo(maxLoanValue) > 0 ) {
            return new MortgageResponse(false, BigDecimal.ZERO, "A Mortgage cannot be more than 4 times the income");
        } else if (request.loanValue().compareTo(request.homeValue()) > 0) {
            return new MortgageResponse(false, BigDecimal.ZERO, "A Mortgage cannot be more than the home value");
        }

        BigDecimal monthlyCost = calculateMonthlyCost(request.loanValue(), request.maturityPeriod());
        if (monthlyCost.compareTo(BigDecimal.ZERO) < 0) {
            return new MortgageResponse(false, BigDecimal.ZERO, "No interest rate found for maturity period: " + request.maturityPeriod());
        }
        MortgageResponse response = new MortgageResponse(true, monthlyCost, "");
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
        BigDecimal interestRate;
        try {
            interestRate = mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(maturityPeriod);
            log.info("Interest rate for maturity period: {} is: {}", maturityPeriod, interestRate);
        } catch (MortgageAndInterestRatesException e) {
            log.error("Error getting interest rate for maturity period: {}", maturityPeriod, e);
            return BigDecimal.valueOf(-1);
        }

        /*
          The formula to calculate the monthly cost of a loan is:
          M = P[r(1+r)^n]/[(1+r)^n-1]
          Where:   M: Monthly mortgage payment.
                   P: Loan value (principal).
                   r: Monthly interest rate percentage (annual interest rate  / (12 * 100))
                      Interest rate is usually in percentage form, so we need to divide it by 100 to get the decimal form.
                      The monthly interest rate decimal is the annual interest rate decimal divided by 12.
                   n: Total number of payments (years * 12).

         */

        BigDecimal monthlyInterestRate =  interestRate.divide(BigDecimal.valueOf(12 * 100), 10,  RoundingMode.HALF_UP);
        log.info("Monthly interest rate: {}", monthlyInterestRate);

        int totalPayments = maturityPeriod * 12;
        log.info("Total payments: {}", totalPayments);

        BigDecimal mnthlyIntrstPlusOnePowTotMnths = monthlyInterestRate.add(BigDecimal.ONE).pow(totalPayments);
        log.info("Monthly interest rate plus one to the power of total months: {}", mnthlyIntrstPlusOnePowTotMnths);

        BigDecimal numerator = monthlyInterestRate.multiply(mnthlyIntrstPlusOnePowTotMnths);
        log.info("Numerator: {}", numerator);

        BigDecimal denominator = mnthlyIntrstPlusOnePowTotMnths.subtract(BigDecimal.ONE);
        log.info("Denominator: {}", denominator);

        BigDecimal monthlyCost = loanValue.multiply(numerator.divide(denominator,  RoundingMode.HALF_UP));
        log.info("Monthly cost: {}", monthlyCost);
        monthlyCost = monthlyCost.setScale(2, RoundingMode.HALF_UP);

        return monthlyCost;
    }
}
