package com.baolan2005.jcampconverter.helpers

class Parser {
    private var datasetHelper: DatasetHelper
    private enum class ENCODED_TYPE {
        NONE, SQZ, DIF, DUP
    }

    init {
        datasetHelper = DatasetHelper()
    }

    fun getNumber(value: String, isDIF: Boolean = false, storedData: Array<Double> = arrayOf()): Double? {
        var doubleNumber = value.toDoubleOrNull()
        if (doubleNumber != null) {
            return doubleNumber
        }

        var convertedStr = ""
        if (!isDIF) {
            convertedStr = datasetHelper.convertSQZ(value)
        }
        else {
            convertedStr = datasetHelper.convertDIF(value)
        }

        doubleNumber = value.toDoubleOrNull()
        if (doubleNumber != null) {
            return doubleNumber
        }

        return null
    }

    private fun getArrayNumber(value: String, encodedType: ENCODED_TYPE, cachingData: String, cachingEncodedType: ENCODED_TYPE, data: Array<Double>) : Array<Double> {
        var result: ArrayList<Double> = arrayListOf<Double>()
        when (encodedType) {
            ENCODED_TYPE.SQZ -> {
                val convertedStr = datasetHelper.convertSQZ(value)
                val doubleNumber = convertedStr.toDoubleOrNull()
                if (doubleNumber != null) {
                    result.add(doubleNumber)
                }
            }
            ENCODED_TYPE.DIF -> {
                val prevValue = data.lastOrNull()
                val difValue = value.toDoubleOrNull()
                if (prevValue != null && difValue != null) {
                    val encodedValue = prevValue + difValue
                    result.add(encodedValue)
                }
            }
            ENCODED_TYPE.DUP -> {
                val prevValue = cachingData.toDoubleOrNull()
                val dupValue = value.toIntOrNull()
                if (prevValue != null && dupValue != null) {
                    repeat(dupValue - 1) {
                        if (cachingEncodedType == ENCODED_TYPE.DIF) {
                            val lastData = data.lastOrNull()
                            if (lastData != null) {
                                result.add(lastData + prevValue)
                            }
                        }
                        else {
                            result.add(prevValue)
                        }
                    }
                }
            }
            else -> {
                val doubleNumber = value.toDoubleOrNull()
                if (doubleNumber != null) {
                    result.add(doubleNumber)
                }
            }

        }

        return result.toTypedArray()
    }

    fun parse(value: String): Pair<Array<Double>, Boolean> {
        val doubleValue = value.toDoubleOrNull()
        if (doubleValue != null) {
            return Pair(arrayOf(doubleValue), false)
        }

        var result: ArrayList<Double> = arrayListOf()

        val arrSplitted = datasetHelper.splitString(value)
        if (arrSplitted.size > 1) {
            for (item in arrSplitted) {
                val number = getNumber(item)
                if (number != null) {
                    result.add(number)
                }
            }
            return Pair(result.toTypedArray(), false)
        }

        val dataCompressedStr = arrSplitted[0]

        var numberStr = ""
        var encodedType = ENCODED_TYPE.NONE

        var isDIF = false
        var cachingData = ""
        var cachingEncodedType = ENCODED_TYPE.NONE

        for (char in dataCompressedStr) {
            val charString = char.toString()
            var decodedChar = ""

            if (char.isDigit() || char == '.') {
                numberStr += char
                continue
            }

            var currEncodedType = ENCODED_TYPE.SQZ

            val sqzVal = SQZ[charString]
            val difVal = DIF[charString]
            val dupVal = DUP[charString]
            if (sqzVal != null) {
                decodedChar = sqzVal.toString()
                currEncodedType = ENCODED_TYPE.SQZ
                isDIF = false
            }
            else if (difVal != null) {
                decodedChar = difVal.toString()
                currEncodedType = ENCODED_TYPE.DIF
                isDIF = true
            }
            else if (dupVal != null) {
                decodedChar = dupVal.toString()
                currEncodedType = ENCODED_TYPE.DUP
            }

            val arrData = getArrayNumber(numberStr, encodedType, cachingData, cachingEncodedType, result.toTypedArray())
            cachingEncodedType = encodedType
            result.addAll(arrData)
            cachingData = numberStr

            numberStr = decodedChar
            encodedType = currEncodedType
        }

        // Process the last char
        val arrData = getArrayNumber(numberStr, encodedType, cachingData, cachingEncodedType, result.toTypedArray())
        cachingEncodedType = encodedType
        result.addAll(arrData)
        cachingData = numberStr


        return Pair(result.toTypedArray(), isDIF)
    }


}