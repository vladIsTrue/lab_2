package org.example;

import junit.framework.TestCase;

/**
 * Unit test for simple Calculator.
 */

public class CalculatorTest extends TestCase {

    public void testCalculate() {
        // Test a simple expression
        String expression = "2 + 3";
        String variables = "";
        int result = Calculator.calculate(expression, variables);
        assertEquals(5, result);

        // Test an expression with variables
        expression = "x + 5";
        variables = "x 10";
        result = Calculator.calculate(expression, variables);
        assertEquals(15, result);

        // Test an expression with multiplication and division
        expression = "3 * 4 / 2";
        variables = "";
        result = Calculator.calculate(expression, variables);
        assertEquals(6, result);

        // Test an expression with parentheses
        expression = "(5 + 3) * 2";
        variables = "";
        result = Calculator.calculate(expression, variables);
        assertEquals(16, result);

        // Test an expression with complex variables and operations
        expression = "a + b * c / 2 - d";
        variables = "a 2 b 3 c 4 d 1";
        result = Calculator.calculate(expression, variables);
        assertEquals(7, result);
    }

    public void testCalculateWithInvalidExpression() {
        // Test an invalid expression
        String expression = "2 + * 3";
        String variables = "";
        try {
            Calculator.calculate(expression, variables);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // This is expected
        }
    }
}
