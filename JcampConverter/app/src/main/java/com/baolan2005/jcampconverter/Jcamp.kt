package com.baolan2005.jcampconverter

import android.util.Log
import java.lang.Exception
import java.net.URL

class Jcamp {
    var spectra: ArrayList<Spectrum> = arrayListOf()
    var labeledDataRecords: ArrayList<HashMap<String, String>> = arrayListOf()

    private var originData: List<String>

    constructor(stringData: String) {

        try {
            val inputStream = URL(stringData).openStream()
            val data = mutableListOf<String>()
            inputStream.bufferedReader(Charsets.US_ASCII).useLines { lines -> lines.forEach { data.add(it) } }
            originData = data
            readData()
        }
        catch (e: Exception) {
            originData = stringData.split("\n")
            readData()
        }
    }

    private fun getSpectrum(arrData: Array<String>, dataRecords: Array<String>) {
        if (arrData.size == 0) {
            return
        }

        val dicDataRecord: HashMap<String, String> = hashMapOf()
        for (record in dataRecords) {
            val values = record.split("=")
            val label = values[0]

            dicDataRecord[label] = if (values.size > 1) values[1] else ""
        }

        val dataFormatValue = dicDataRecord["DATAFORMAT"]?.split(",")?.first() ?: ""

        var firstXValue = 0.0; var lastXValue = 0.0
        var factorXValue = 1.0; var factorYValue = 1.0

        dicDataRecord["##FIRSTX"]?.let { firstX ->
            val firstXStr = firstX.replace(" ", "")
            firstXValue = firstXStr.toDoubleOrNull() ?: 0.0
        } ?: run {
            dicDataRecord["##FIRST"]?.let { first ->
                val firstStr = first.replace(" ", "")
                val arrFirst = firstStr.split(",")
                val firstX = arrFirst[0]
                val firstR = arrFirst[1]
                val firstI = arrFirst[2]
                dicDataRecord["##FIRSTX"] = firstX
                dicDataRecord["##FIRSTR"] = firstR
                dicDataRecord["##FIRSTI"] = firstI

                firstXValue = firstX.toDoubleOrNull() ?: 0.0
            } ?: run {
                val prevDicDataRecord = labeledDataRecords.lastOrNull()
                val firstX = prevDicDataRecord?.get("##FIRSTX")
                firstXValue = firstX?.toDoubleOrNull() ?: 0.0
            }
        }

        dicDataRecord["##LASTX"]?.let { lastX ->
            val lastXStr = lastX.replace(" ", "")
            lastXValue = lastXStr.toDoubleOrNull() ?: 0.0
        } ?: run {
            dicDataRecord["##LAST"]?.let { last ->
                val lastStr = last.replace(" ", "")
                val arrLast = lastStr.split(",")
                val lastX = arrLast[0]
                val lastR = arrLast[1]
                val lastI = arrLast[2]
                dicDataRecord["##LASTX"] = lastX
                dicDataRecord["##LASTR"] = lastR
                dicDataRecord["##LASTI"] = lastI

                lastXValue = lastX.toDoubleOrNull() ?: 0.0
            } ?: run {
                val prevDicDataRecord = labeledDataRecords.lastOrNull()
                val lastX = prevDicDataRecord?.get("##LASTX")
                lastXValue = lastX?.toDoubleOrNull() ?: 0.0
            }
        }

        dicDataRecord["##XFACTOR"]?.let { factorX ->
            val factorXStr = factorX.replace(" ", "")
            factorXValue = factorXStr.toDoubleOrNull() ?: 1.0
        } ?: run {
            dicDataRecord["##FACTOR"]?.let { factor ->
                val factorStr = factor.replace(" ", "")
                val arrFactors = factorStr.split(",")
                val factorX = arrFactors[0]
                val factorR = arrFactors[1]
                val factorI = arrFactors[2]
                dicDataRecord["##XFACTOR"] = factorX
                dicDataRecord["##RFACTOR"] = factorR
                dicDataRecord["##IFACTOR"] = factorI

                factorXValue = factorX.toDoubleOrNull() ?: 1.0
            } ?: run {
                val prevDicDataRecord = labeledDataRecords.lastOrNull()
                val factorX = prevDicDataRecord?.get("##XFACTOR")
                factorXValue = factorX?.toDoubleOrNull() ?: 1.0
            }
        }

        dicDataRecord["##YFACTOR"]?.let { factorY ->
            val factorYStr = factorY.replace(" ", "")
            factorYValue = factorYStr.toDoubleOrNull() ?: 1.0
        } ?: run {
            dicDataRecord["##RFACTOR"]?.let { factorR ->
                factorYValue = factorR.toDoubleOrNull() ?: 1.0
            } ?: run {
                val prevDicDataRecord = labeledDataRecords.lastOrNull()
                val factorI = prevDicDataRecord?.get("##IFACTOR")
                factorYValue = factorI?.toDoubleOrNull() ?: 1.0
            }
        }

        val data = arrData.joinToString("\n")
        val spectrum = Spectrum(data, dataFormatValue, factorXValue, factorYValue, firstXValue, lastXValue )
        spectra.add(spectrum)
        labeledDataRecords.add(dicDataRecord)
    }

    private fun readData() {
        var readingData = false
        var storeDataForReading: ArrayList<String> = arrayListOf()
        var storeLabelDataRecords: ArrayList<String> = arrayListOf()
        for (line in originData) {
            val trimmedLine = line.trim()
            if (trimmedLine == "") {
                //ignore empty line
                continue
            }
            if (trimmedLine.startsWith("##")) {
                if (trimmedLine.startsWith("##XYDATA=") || trimmedLine.startsWith("##XYPOINTS=") || trimmedLine.startsWith("##PEAK TABLE=") || trimmedLine.startsWith("##PEAK ASSIGNMENTS=") || trimmedLine.startsWith("##DATA TABLE=")) {

                    val seperatedLine = trimmedLine.split("=")
                    val dataFormatStr = "DATAFORMAT=" + seperatedLine[1]
                    storeLabelDataRecords.add(dataFormatStr)

                    if (storeDataForReading.size > 0) {
                        getSpectrum(storeDataForReading.toTypedArray(), storeLabelDataRecords.toTypedArray())
                        storeDataForReading = arrayListOf()
                    }
                    readingData = true
                }
                else {
                    readingData = false
                    getSpectrum(storeDataForReading.toTypedArray(), storeLabelDataRecords.toTypedArray())

                    if (storeDataForReading.size > 0) {
                        storeLabelDataRecords = arrayListOf()
                    }

                    storeDataForReading = arrayListOf()

                    storeLabelDataRecords.add(trimmedLine)
                }
            }
            else if (!trimmedLine.startsWith("$") && readingData) {
                storeDataForReading.add(trimmedLine)
            }
            else {
                readingData = false
                getSpectrum(storeDataForReading.toTypedArray(), storeLabelDataRecords.toTypedArray())

                if (storeDataForReading.size > 0) {
                    storeLabelDataRecords = arrayListOf()
                }

                storeDataForReading = arrayListOf()

                storeLabelDataRecords.add(trimmedLine)

            }
        }
    }
}