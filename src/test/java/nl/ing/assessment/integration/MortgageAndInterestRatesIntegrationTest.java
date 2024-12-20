package nl.ing.assessment.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MortgageAndInterestRatesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetInterestRates() throws Exception {
        mockMvc.perform(get("/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCheckMortgage() throws Exception {
        String requestJson = "{\"loanValue\":10000,\"maturityPeriod\":10,\"income\":30000,\"homeValue\":50000}";
        String responseJson = "{\"feasible\":true,\"monthlyCost\":101.25,\"error\":\"\"}";

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_NofeasilibiltyLoanMoreThanHome() throws Exception {
        String requestJson = """
                        {
                          "income": 30000,
                          "maturityPeriod": 10,
                          "loanValue": "100000",
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
    public void testCheckMortgage_NofeasilibiltyLoanMoreThanIncome() throws Exception {
        String requestJson = """
                        {
                          "income": 3000,
                          "maturityPeriod": 10,
                          "loanValue": "100000",
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
    public void testCheckMortgage_NoInterestRateFoundForMaturity() throws Exception {
        String requestJson = """
                        {
                          "income": 30000,
                          "maturityPeriod": 50,
                          "loanValue": "10000",
                          "homeValue": "50000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "Interest rate not found for maturity period: 50"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_IncomeIsZero() throws Exception {
        String requestJson = """
                        {
                          "income": 0,
                          "maturityPeriod": 50,
                          "loanValue": "10000",
                          "homeValue": "50000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "An income must be greater than zero"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_LoanIsZero() throws Exception {
        String requestJson = """
                        {
                          "income": 2000,
                          "maturityPeriod": 50,
                          "loanValue": "0",
                          "homeValue": "50000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "A loan value must be greater than zero"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_HomeValueIsZero() throws Exception {
        String requestJson = """
                        {
                          "income": 2000,
                          "maturityPeriod": 50,
                          "loanValue": "200000",
                          "homeValue": "0"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "A home value must be greater than zero"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void testCheckMortgage_MaturityPeriodIsZero() throws Exception {
        String requestJson = """
                        {
                          "income": 2000,
                          "maturityPeriod": 0,
                          "loanValue": "200000",
                          "homeValue": "600000"
                        }
                        """;
        String responseJson =  """
                        {
                           "feasible": false,
                           "monthlyCost": 0,
                           "error": "A maturity period must be greater than zero"
                         }
                      """;

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }
}

