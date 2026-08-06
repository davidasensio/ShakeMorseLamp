package com.handysparksoft.shakelamp.core.morse.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConvertTextToMorseUseCaseTest {
    private val convertTextToMorse = ConvertTextToMorseUseCase()

    @Test
    fun `encodes a single letter`() {
        assertEquals(".", convertTextToMorse("e"))
    }

    @Test
    fun `encodes letters case-insensitively`() {
        assertEquals(convertTextToMorse("sos"), convertTextToMorse("SOS"))
    }

    @Test
    fun `encodes numbers`() {
        assertEquals("----- .----", convertTextToMorse("01"))
    }

    @Test
    fun `encodes punctuation`() {
        assertEquals(".-.-.-", convertTextToMorse("."))
    }

    @Test
    fun `separates words with a slash token`() {
        assertEquals("... --- ... / ... --- ...", convertTextToMorse("sos sos"))
    }

    @Test
    fun `collapses repeated whitespace into one separator`() {
        assertEquals(convertTextToMorse("a b"), convertTextToMorse("a    b"))
    }

    @Test
    fun `strips accents before encoding`() {
        assertEquals(convertTextToMorse("e"), convertTextToMorse("é"))
    }

    @Test
    fun `ignores characters with no morse mapping`() {
        assertEquals(convertTextToMorse("ab"), convertTextToMorse("a#b"))
    }

    @Test
    fun `blank input encodes to an empty string`() {
        assertEquals("", convertTextToMorse("   "))
    }
}
