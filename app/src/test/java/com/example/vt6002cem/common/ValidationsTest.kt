package com.example.vt6002cem.common

import org.junit.Assert.*

import org.junit.Test

class ValidationsTest {

    @Test
    fun email() {
        val expectEmpty = ""
        val expectTrue = "dge123@gmail.com"
        val expectFalse = "dge123gmail.com"

        val resultEmpty = Validations.email(expectEmpty)
        val resultTrue = Validations.email(expectTrue)
        val resultFalse = Validations.email(expectFalse)

        assertEquals("Please fill the email field", resultEmpty)
        assertEquals(null, resultTrue)
        assertEquals("Please input validate email", resultFalse)
    }

    @Test
    fun password() {
        val expectEmpty = ""
        val expectTrue = "Abcd1231234123"
        val expectFalse = "abcd"

        val resultEmpty = Validations.password(expectEmpty)
        val resultTrue = Validations.password(expectTrue)
        val resultFalse = Validations.password(expectFalse)

        assertEquals("Please fill the password field", resultEmpty)
        assertEquals(null, resultTrue)
        assertEquals("Minimum eight characters, at least one letter and one number", resultFalse)
    }

    @Test
    fun confirmPassword() {
        val password = "Abcd1231234123"
        val expectEmpty = ""
        val expectTrue = "Abcd1231234123"
        val expectFalse = "abcd"

        val resultEmpty = Validations.confirmPassword(expectEmpty, password)
        val resultTrue = Validations.confirmPassword(expectTrue, password)
        val resultFalse = Validations.confirmPassword(expectFalse, password)

        assertEquals("Please fill the password field", resultEmpty)
        assertEquals(null, resultTrue)
        assertEquals("Please match with password", resultFalse)
    }

    @Test
    fun name() {
        val expectEmpty = ""
        val expectTrue = "dennis"
        val expectFalse = "123124"

        val resultEmpty = Validations.name(expectEmpty)
        val resultTrue = Validations.name(expectTrue)
        val resultFalse = Validations.name(expectFalse)

        assertEquals("Please fill the name field", resultEmpty)
        assertEquals(null, resultTrue)
        assertEquals("Please input validate name", resultFalse)
    }

    @Test
    fun text() {
        val expectEmpty = ""
        val expectTrue = "dennis"

        val resultEmpty = Validations.text(expectEmpty)
        val resultTrue = Validations.text(expectTrue)

        assertEquals("Please fill the field", resultEmpty)
        assertEquals(null, resultTrue)
    }

    @Test
    fun number() {
        val expectEmpty = null
        val expectTrue = 12

        val resultEmpty = Validations.number(expectEmpty)
        val resultTrue = Validations.number(expectTrue)

        assertEquals("Please fill the number field", resultEmpty)
        assertEquals(null, resultTrue)
    }
}

