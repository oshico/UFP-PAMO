package edu.ufp.pam.examples.p00_helpercalculator

class CalculatorHelper {
    companion object {
        fun add(a: Double, b: Double): Double {
            return a + b;
        }

        fun subract(a: Double, b: Double): Double {
            return a - b;
        }

        fun multiply(a: Double, b: Double): Double {
            return a * b;
        }

        fun divide(a: Double, b: Double): Double {
            if (b == 0.0) throw IllegalArgumentException("Cannot divide by 0 (zero)!");
            return a / b;
        }
    }
}