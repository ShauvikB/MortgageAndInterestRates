package nl.ing.assessment.service;

import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import nl.ing.assessment.mortgage.response.MortgageResponse;
import nl.ing.assessment.util.MortgageAndInterestRatesUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MortgageAndInterestRatesServiceTest {

    @Mock
    private MortgageAndInterestRatesUtil mortgageAndInterestRatesUtil;

    @Autowired
    @InjectMocks
    private MortgageAndInterestRatesService mortgageAndInterestRatesService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testCheckMortgage_Feasible() throws MortgageAndInterestRatesException {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("30000"), new BigDecimal("50000"));
        when(mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10)).thenReturn(new BigDecimal("3.0"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertEquals(true, response.feasible());
        assertEquals(new BigDecimal("289.68"), response.monthlyCost());
    }

    @Test
    public void testCheckMortgage_NotFeasible() throws MortgageAndInterestRatesException {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("50000"), new BigDecimal("30000"));
        when(mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10)).thenReturn(new BigDecimal("3.0"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertEquals(false, response.feasible());
        assertEquals(new BigDecimal("482.80"), response.monthlyCost());
    }

    @Test
    public void testCheckMortgage_InterestRateNotFound() throws MortgageAndInterestRatesException {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("30000"), new BigDecimal("50000"));
        when(mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10)).thenThrow(new MortgageAndInterestRatesException("Interest rate not found"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertEquals(true, response.feasible());
        assertEquals(new BigDecimal("289.68"), response.monthlyCost());
    }
}