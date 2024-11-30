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

Business Validations Added
****************************
A String type error is added to mortgage response along with A Mortgage cannot be more than 4 times the income. This is for end user to determine why a mortgage is not feasible.
- If a mortgage exceeds 4 times the income
   The response has error message "A Mortgage cannot be more than 4 times the income", feasibale false, monthlyCost as 0
- If a mortgage exceeds the home value
  The response has error message "A Mortgage cannot be more than the home value", feasibale false, monthlyCost as 0
- if loan value is 0 or less than 0
  The response has error message "A loan value must be greater than zero", feasibale false, monthlyCost as 0
- If Imcome is 0 or less than 0
  The response has error message "An income must be greater than zero", feasibale false, monthlyCost as 0
- If home value is 0 or less than 0
  The response has error message "A home value must be greater than zero", feasibale false, monthlyCost as 0
- If maturity period is 0 or less than 0
  The response has error message "A maturity period must be greater than zero", feasibale false, monthlyCost as 0
- If maturityPeriod provided does not have any respective interest rates
  The response has error message "No interest rate found for maturity period: [Maturity Period]", feasibale false, monthlyCost as 0

The formula used to calculate the monthly cost of a loan is:

 M = P[r(1+r)^n]/[(1+r)^n-1]
 
Where:   

         M: Monthly mortgage payment.
         P: Loan value (principal).
         r: Monthly interest rate percentage (annual interest rate  / (12 * 100))
            Interest rate is usually in percentage form, so we need to divide it by 100 to get the decimal form.
            The monthly interest rate decimal is the annual interest rate decimal divided by 12.
         n: Total number of payments (years * 12).
         
Api Details 
************

    Get Interest Rates
    *******************
    url - api/interest-rates

    Request Body - Blank


    Mortgage Check
    ***************
    url - api/mortgage-check

    Request Body - 

    {
      "income": 30000,
      "maturityPeriod": 20,
      "loanValue": "5000000",
      "homeValue": "50000"
    }

