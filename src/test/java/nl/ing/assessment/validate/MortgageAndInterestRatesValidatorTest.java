package nl.ing.assessment.validate;

import nl.ing.assessment.exception.MortgageAndInterestRatesException;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MortgageAndInterestRatesValidatorTest {

    @Autowired
    private MortgageAndInterestRatesValidator validator;

    @ParameterizedTest
    @MethodSource("provideInvalidMortgageRequests")
    @DisplayName("Test validateMortgageDetails with invalid inputs")
    void testValidateMortgageDetailsWithInvalidInputs(MortgageRequest request, String expectedMessage) {
        MortgageAndInterestRatesException exception = assertThrows(MortgageAndInterestRatesException.class, () -> {
            validator.validateMortgageDetails(request);
        });
        assert exception.getMessage().contains(expectedMessage);
    }

    private static Stream<Arguments> provideInvalidMortgageRequests() {
        return Stream.of(
                Arguments.of(new MortgageRequest(BigDecimal.ZERO, 30, BigDecimal.valueOf(30), BigDecimal.valueOf(400000)), "An income must be greater than zero"),
                Arguments.of(new MortgageRequest(BigDecimal.valueOf(100000), 20, BigDecimal.valueOf(30), BigDecimal.ZERO), "A home value must be greater than zero"),
                Arguments.of(new MortgageRequest(BigDecimal.valueOf(100000), 0, BigDecimal.ZERO, BigDecimal.valueOf(400000)), "A maturity period must be greater than zero"),
                Arguments.of(new MortgageRequest(BigDecimal.valueOf(100000), 10, BigDecimal.ZERO, BigDecimal.valueOf(400000)), "A loan value must be greater than zero"),
                Arguments.of(new MortgageRequest(BigDecimal.valueOf(400), 20, BigDecimal.valueOf(30000), BigDecimal.valueOf(400000)), "A Mortgage cannot be more than 4 times the income"),
                Arguments.of(new MortgageRequest(BigDecimal.valueOf(100000), 10, BigDecimal.valueOf(300000), BigDecimal.valueOf(40000)), "A Mortgage cannot be more than the home value")
        );
    }
}
