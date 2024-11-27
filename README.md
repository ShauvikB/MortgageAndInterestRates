# MortgageAndInterestRates
Java based backend application to get a list of current interest rates and calculate for a mortgage check

# Application
Contains the following endpoints;
* GET /api/interest-rates (get a list of current interest rates)
* POST /api/mortgage-check (post the parameters to calculate for a mortgage check)

The list of current mortgage rates is created in memory on application startup.
The mortgage rate object contains the fields; maturityPeriod (integer), interestRate (Percentage) and lastUpdate (Timestamp)
The posted data for the mortgage check contains at least the fields; income (Amount), maturityPeriod (integer), loanValue (Amount), homeValue (Amount).

The mortgage check return if the mortgage is feasible (boolean) and the
montly costs (Amount) of the mortgage.
Business rules that apply are
- a mortgage does not exceed 4 times the income
- a mortgage does not exceed the home value
