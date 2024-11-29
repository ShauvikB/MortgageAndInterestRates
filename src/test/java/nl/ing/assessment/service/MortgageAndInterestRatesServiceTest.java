package nl.ing.assessment.service;

import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import nl.ing.assessment.mortgage.response.MortgageResponse;
import nl.ing.assessment.util.MortgageAndInterestRatesUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MortgageAndInterestRatesServiceTest {

    @MockBean
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
        when(mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10)).thenReturn(new BigDecimal("2.0"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertTrue(response.feasible());
        assertEquals(new BigDecimal("276.04"), response.monthlyCost());
    }

    @Test
    public void testCheckMortgage_NotFeasible_LoanMoreThanIncome()  {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("50000"), new BigDecimal("30000"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertFalse(response.feasible());
        assertEquals(new BigDecimal("0"), response.monthlyCost());
        assertEquals("A Mortgage cannot be more than 4 times the income", response.error());
    }

    @Test
    public void testCheckMortgage_NotFeasible_LoanMoreThanHomeValue()  {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("40000"), new BigDecimal("30000"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertFalse(response.feasible());
        assertEquals(new BigDecimal("0"), response.monthlyCost());
        assertEquals("A Mortgage cannot be more than the home value", response.error());
    }

    @Test
    public void testCheckMortgage_InterestRateNotFound() throws MortgageAndInterestRatesException {
        MortgageRequest request = new MortgageRequest(new BigDecimal("10000"), 10, new BigDecimal("30000"), new BigDecimal("50000"));
        when(mortgageAndInterestRatesUtil.getInterestRateForMaturityPeriod(10)).thenThrow(new MortgageAndInterestRatesException("Interest rate not found for maturity period: 10"));

        MortgageResponse response = mortgageAndInterestRatesService.checkMortgage(request);

        assertFalse(response.feasible());
        assertEquals(BigDecimal.ZERO, response.monthlyCost());
        assertEquals("No interest rate found for maturity period: 10", response.error());
    }
}