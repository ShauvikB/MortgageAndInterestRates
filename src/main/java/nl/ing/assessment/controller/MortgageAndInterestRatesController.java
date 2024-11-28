package nl.ing.assessment.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ing.assessment.interest.model.InterestRate;
import nl.ing.assessment.mortgage.request.MortgageRequest;
import nl.ing.assessment.mortgage.response.MortgageResponse;
import nl.ing.assessment.service.MortgageAndInterestRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MortgageAndInterestRatesController {

    @Autowired
    private MortgageAndInterestRatesService mortgageAndInterestRatesService;

    @GetMapping("/interest-rates")
    public List<InterestRate> getInterestRates() {
        return mortgageAndInterestRatesService.getAllInterestRates();
    }

    @PostMapping("/mortgage-check")
    public MortgageResponse checkMortgage(@RequestBody MortgageRequest request) {
        return mortgageAndInterestRatesService.checkMortgage(request);
    }
}
