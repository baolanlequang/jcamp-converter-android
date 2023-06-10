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
//            if let dupValue = DUP[charString] {
//                let prevChar = value[idx-1]
//                let newChars = Array(repeating: prevChar, count: dupValue-1)
//                convertedStr += newChars.joined()
//            }
//            else {
//                convertedStr += charString
//            }
        }
        return convertedStr
    }

//    func convertDUP(_ value: String) -> String {
//        var convertedStr = ""
//
//        for (idx, char) in value.enumerated() {
//            let charString = String(char)
//            if let dupValue = DUP[charString] {
//                let prevChar = value[idx-1]
//                let newChars = Array(repeating: prevChar, count: dupValue-1)
//                convertedStr += newChars.joined()
//            }
//            else {
//                convertedStr += charString
//            }
//        }
//
//        return convertedStr
//    }
}