package nl.ing.assessment.util;

import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MortgageAndInterestRatesUtilTest {

    @Autowired
    private MortgageAndInterestRatesUtil mortgageAndInterestRatesUtil;

    @BeforeEach
    public void setUp() {
        mortgageAndInterestRatesUtil.loadInterestRates();
    }

    @Test
    public void testGetInterestRate_Success() throws MortgageAndInterestRatesException {
        BigDecimal interestRate = mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10);
        assertEquals(new BigDecimal("3.0"), interestRate);
    }

    @Test
    public void testGetInterestRate_NotFound() {
        MortgageAndInterestRatesException exception = assertThrows(MortgageAndInterestRatesException.class, () -> {
            mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(40);
        });
        assertEquals("Interest rate not found for maturity period: 40", exception.getMessage());
    }
}
