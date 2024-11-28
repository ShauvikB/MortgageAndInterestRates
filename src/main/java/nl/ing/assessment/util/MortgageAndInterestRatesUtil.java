package nl.ing.assessment.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.interest.model.InterestRate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@NoArgsConstructor
@Getter
@Slf4j
public class MortgageAndInterestRatesUtil {

    private List<InterestRate> interestRates;

    /**
     * Load a List of interest rates
     */
    @PostConstruct
    public void loadInterestRates() {
        log.info("Initializing interest rates");
        interestRates = new ArrayList<>();
        interestRates.add(new InterestRate(10, new BigDecimal("3.0"), new Timestamp(System.currentTimeMillis())));
        interestRates.add(new InterestRate(20, new BigDecimal("3.5"), new Timestamp(System.currentTimeMillis())));
        interestRates.add(new InterestRate(30, new BigDecimal("4.0"), new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Get interest rate for maturity period
     *
     * @param maturityPeriod  Maturity period
     * @return Interest rate
     * @throws MortgageAndInterestRatesException
     */
    public BigDecimal getInterestRateForMaturityPeriod(Integer maturityPeriod) throws MortgageAndInterestRatesException {
       log.info("Getting interest rate for maturity period: {}", maturityPeriod);
        return interestRates.stream()
                .filter(interestRate -> Objects.equals(interestRate.maturityPeriod(), maturityPeriod))
                .findFirst()
                .map(InterestRate::interestRate)
                .orElseThrow(() -> new MortgageAndInterestRatesException("Interest rate not found for maturity period: " + maturityPeriod));
    }
}