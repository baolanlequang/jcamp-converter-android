package com.baolan2005.jcampconverter.helpers

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DatasetHelperTest {
    private lateinit var datasetHelper: DatasetHelper

    @Before
    fun createHelper() {
        datasetHelper = DatasetHelper()
    }

    @Test
    fun testIsAFFN() {
        val arrValues = arrayOf("1", "19", "999", "+10", "-10", ".10", "+.10", "-.10", "1e2", "1E234", "+1.0")
        for (value in arrValues) {
            val isAFFN = datasetHelper.isAFFN(value)
            assertTrue(isAFFN)
        }
    }

    @Test
    fun testIsNotAFFN() {
        val arrValues = arrayOf("", "@19", "9#99", "-10?", "@.10", "-.1.00", "1E2E34")
        for (value in arrValues) {
            val isAFFN = datasetHelper.isAFFN(value)
            assertFalse(isAFFN)
        }
    }

    @Test
    fun testConvertDUP() {
        val arrValues = arrayOf("", "12", "1S", "1T", "1U", "1V", "1W", "1X", "1Y", "1Z", "1s", "12S", "12T", "12U", "12V", "12W", "12X", "12Y", "12Z", "12s")
        val expectedValues = arrayOf("", "12", "1", "11", "111", "1111", "11111", "111111", "1111111", "11111111", "111111111", "12", "122", "1222", "12222", "122222", "1222222", "12222222", "122222222", "1222222222")

        for ((idx, value) in arrValues.withIndex()) {
            val convetedDUP = datasetHelper.convertDUP(value)
            val expected = expectedValues[idx]

            assertEquals(expected, convetedDUP)
        }
    }

    @Test
    fun testConvertSQZ() {
        val arrValues = arrayOf("", "12", "@", "1A", "1B", "1C", "1D", "1E", "1F", "1G", "1H", "1I", "1a", "1b", "1c", "1d", "1e", "1f", "1g", "1h", "1i")
        val expectedValues = arrayOf("", "12", "0", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1-1", "1-2", "1-3", "1-4", "1-5", "1-6", "1-7", "1-8", "1-9")

        for ((idx, value) in arrValues.withIndex()) {
            val convetedSQZ = datasetHelper.convertSQZ(value)
            val expected = expectedValues[idx]

            assertEquals(expected, convetedSQZ)
        }
    }
//
//    func testConvertedDIF() throws {
//        let arrValues = ["", "1%", "1J", "1K", "1L", "1M", "1N", "1O", "1P", "1Q", "1R", "1j", "1k", "1l", "1m", "1n", "1o", "1p", "1q", "1r"]
//        let expectedValues = ["", "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0", "9.0", "10.0", "0.0", "-1.0", "-2.0", "-3.0", "-4.0", "-5.0", "-6.0", "-7.0", "-8.0"]
//        for (idx, value) in arrValues.enumerated() {
//            let convetedDIF = datasetHelper.convertDIF(value)
//            let expected = expectedValues[idx]
//
//            XCTAssertEqual(convetedDIF, expected)
//        }
//    }
//
//    func testSplitFIXForm() throws {
//        let strValue = "987654321.25 987654321    +10          -11"
//        let expected = ["987654321.25", "987654321", "+10", "-11"]
//        let splitted = datasetHelper.splitString(strValue)
//        XCTAssertEqual(splitted, expected)
//    }
//
//    func testSplitPACForm() throws {
//        let strValue = "1+10-11"
//        let expected = ["1","+10","-11"]
//        let splitted = datasetHelper.splitString(strValue)
//        XCTAssertEqual(splitted, expected)
//    }
}