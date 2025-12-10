package edu.ufp.pam.examples.p00_helpercalculator

import org.junit.Test

import org.junit.Assert.*

class ExampleUnitTestCalculator {
    @Test
    fun calculatorCheckHelperCalculatorAddMethod() {
        val expectedAdition = "3.0"
        assertEquals(expectedAdition, "%.1f".format(CalculatorHelper.add(1.0, 2.0)))
    }

    @Test
    fun calculatorCheckHelperCalculatorSubMethod() {
        val expectedSubtraction = "3.0"
        assertEquals(expectedSubtraction, "%.1f".format(CalculatorHelper.subract(4.0, 1.0)))
    }
}
