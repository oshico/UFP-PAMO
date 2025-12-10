package edu.ufp.pam.examples.p00_helperemailvalidator

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTestEmailValidator {

    @Test
    fun emailValidator_CheckSimpleEmail() {
        assertEquals(EmailValidatorHelper.isValidEmail("name@email.com"), true)
    }

    @Test
    fun emailValidator_CheckGivenDomain() {
        assertTrue(EmailValidatorHelper.isValidEmail("name@email.co.uk"));
    }

    @Test
    fun emailValidator_CheckInvalidDomain() {
        assertTrue(EmailValidatorHelper.isValidEmail("name@email"));
    }

    @Test
    fun emailValidator_InvalidDotsUse() {
        assertFalse(EmailValidatorHelper.isValidEmail("name@email..com"));
    }

    @Test
    fun emailValidator_InvalidUsername() {
        assertFalse(EmailValidatorHelper.isValidEmail("@email.com"));
    }

    @Test
    fun emailValidator_InvalidEmptyEmail() {
        assertFalse(EmailValidatorHelper.isValidEmail(""));
    }

    @Test
    fun passValidator_InvalidPassLength() {
        assertFalse(EmailValidatorHelper.isValidPassword("123456"));
    }
}