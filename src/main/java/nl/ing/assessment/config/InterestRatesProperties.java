package nl.ing.assessment.config;

import lombok.Getter;
import lombok.Setter;
import nl.ing.assessment.interest.model.InterestRate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "interest")
@Getter
@Setter
public class InterestRatesProperties {
    private List<InterestRateValue> rates;

    @Getter
    @Setter
    public static class InterestRateValue {
        private int maturityPeriod;
        private BigDecimal rate;
    }

    public List<InterestRate> populateInterestRates() {
        return rates.stream()
                .map(interestRateValue -> new InterestRate(interestRateValue.getMaturityPeriod(), interestRateValue.getRate(), new Timestamp(System.currentTimeMillis())))
                .collect(Collectors.toList());
    }
}