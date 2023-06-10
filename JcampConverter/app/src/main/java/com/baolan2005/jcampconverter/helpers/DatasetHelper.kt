package com.baolan2005.jcampconverter.helpers

import android.util.Log

class DatasetHelper {
    fun isAFFN(value: String): Boolean {
        if (value == "") {
            return false
        }

        val regexPattern = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([Ee][+-]?\\d+)?$"
        val regex = Regex(regexPattern)
        val matches = regex.findAll(value)
        return matches.count() > 0
    }

    fun convertDUP(value: String): String {
        var convertedStr = ""

        for ((idx, char) in value.withIndex()) {
            val charString = char.toString()
            DUP[charString]?.let { dupValue ->
                val prevChar = value[idx-1]
                val newChars = Array(dupValue-1) { prevChar }
                convertedStr += newChars.joinToString("")
            } ?: run {
                convertedStr += charString
            }
        }
        return convertedStr
    }

    fun convertSQZ(value: String): String {
        var convertedStr = ""

        for (char in value) {
            val charString = char.toString()
            SQZ[charString]?.let { sqzValue ->
                convertedStr += sqzValue
            } ?: run {
                convertedStr += charString
            }
        }
        return convertedStr
    }
}