package com.baolan2005.jcampconverter.helpers

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ParserTest {
    private lateinit var parser: Parser

    @Before
    fun createParser() {
        parser = Parser()
    }

    @Test
    fun testParseStringOfNumber() {
        val strValue = "987654321.25"
        val expected = Pair(arrayOf(987654321.25), false) // (first is data, second is false)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParseStringSeperateNumbersWithSpaces() {
        val strValue = "987654321.25 987654321    +10          -11"
        val expected = Pair(arrayOf(987654321.25, 987654321.0, 10.0, -11.0), false)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParseOnlyPACString() {
        val strValue = "1+10-11"
        val expected = Pair(arrayOf(1.0, 10.0, -11.0), false)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParsePACCombinedString() {
        val strValue = "1+10-11 987654321.25 987654321    +10          -11"
        val expected = Pair(arrayOf(1.0, 10.0, -11.0, 987654321.25, 987654321.0, 10.0, -11.0), false)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParseOnlySQZString() {
        val strValue = "1BCCBA@abc"
        val expected = Pair(arrayOf(1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0), false)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParseDIFString() {
        val strValue = "1JJ%jjjjjj"
        val expected = Pair(arrayOf(1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0), true)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }

    @Test
    fun testParseDIFDUPString() {
        val strValue = "4879C1556N0TN9SM9SN3SK9SL7SK9SK7SL7SJ2SJSJ0Sj5Sj3Sj7Sk9Sk6Sl0Sm7Sl8S"
        val expected = Pair(arrayOf(4879.0, 31556.0, 31606.0, 31656.0, 31715.0, 31764.0, 31817.0, 31846.0, 31883.0, 31912.0, 31939.0, 31976.0, 31988.0, 31989.0, 31999.0, 31984.0, 31971.0, 31954.0, 31925.0, 31899.0, 31869.0, 31822.0, 31784.0), true)

        val parsedValue = parser.parse(strValue)

        assertArrayEquals(expected.first, parsedValue.first)
        assertEquals(expected.second, parsedValue.second)
    }
}