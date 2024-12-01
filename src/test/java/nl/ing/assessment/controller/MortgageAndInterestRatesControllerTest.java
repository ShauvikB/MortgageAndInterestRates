package nl.ing.assessment.controller;

import nl.ing.assessment.interest.model.InterestRate;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import nl.ing.assessment.mortgage.response.MortgageResponse;
import nl.ing.assessment.service.MortgageAndInterestRatesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MortgageAndInterestRatesController.class)
public class MortgageAndInterestRatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MortgageAndInterestRatesService mortgageAndInterestRatesService;

    @Test
    public void testGetInterestRates() throws Exception {
        Timestamp fixedTimestamp = Timestamp.valueOf("2024-11-29 11:29:07.265");
        InterestRate mockInterestRate = new InterestRate(10, new BigDecimal("3.0"), fixedTimestamp);
        when(mortgageAndInterestRatesService.getAllInterestRates()).thenReturn(java.util.List.of(mockInterestRate));
        String interestRateJson =  """
                        [{
                           "maturityPeriod": 10,
                           "interestRate": 3.0,
                           "lastUpdate": "2024-11-29T10:29:07.265+00:00"
                         }]
                      """;


        mockMvc.perform(get("/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(content().json(interestRateJson));
    }

    @Test
    public void testCheckMortgage() throws Exception {
        MortgageResponse mockResponse = new MortgageResponse(true, new BigDecimal("96.56"), "");
        when(mortgageAndInterestRatesService.checkMortgage(any(MortgageRequest.class))).thenReturn(mockResponse);

        String requestJson = """
                        {
                          "income": 30000,
                          "maturityPeriod": 10,
                          "loanValue": "10000",
                          "homeValue": "50000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": true,
                           "monthlyCost": 96.56,
                           "error": ""
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_NoFeasibleLoanMoreThanIncome() throws Exception {
        MortgageResponse mockResponse = new MortgageResponse(false, new BigDecimal("0"), "A Mortgage cannot be more than 4 times the income");
        when(mortgageAndInterestRatesService.checkMortgage(any(MortgageRequest.class))).thenReturn(mockResponse);

        String requestJson = """
                        {
                          "income": 100000,
                          "maturityPeriod": 10,
                          "loanValue": "3000",
                          "homeValue": "2000000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "A Mortgage cannot be more than 4 times the income"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_NoFeasibleLoanMoreThanHomeValue() throws Exception {
        MortgageResponse mockResponse = new MortgageResponse(false, new BigDecimal("0"), "A Mortgage cannot be more than the home value");
        when(mortgageAndInterestRatesService.checkMortgage(any(MortgageRequest.class))).thenReturn(mockResponse);

        String requestJson = """
                        {
                          "income": 100000,
                          "maturityPeriod": 10,
                          "loanValue": "30000",
                          "homeValue": "2000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "A Mortgage cannot be more than the home value"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_NoInterestRateFoundForMaturity() throws Exception {
        MortgageResponse mockResponse = new MortgageResponse(false, new BigDecimal("0"), "No interest rate found for maturity period:: 10");
        when(mortgageAndInterestRatesService.checkMortgage(any(MortgageRequest.class))).thenReturn(mockResponse);

        String requestJson = """
                        {
                          "income": 30000,
                          "maturityPeriod": 10,
                          "loanValue": "10000",
                          "homeValue": "50000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "No interest rate found for maturity period:: 10"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }
}
