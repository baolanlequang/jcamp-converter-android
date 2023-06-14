package com.baolan2005.jcampconverter

import com.baolan2005.jcampconverter.helpers.Parser

class Spectrum {
    private var dataString: String

    private var parser: Parser

    private var listX: ArrayList<ArrayList<Double>> = arrayListOf()
    private var listY: ArrayList<ArrayList<Double>> = arrayListOf()
    private var factorX: Double = 1.0
    private var factorY: Double = 1.0
    private var firstX: Double = 0.0
    private var lastX: Double = 0.0

    constructor(data: String, dataFormat: String, factorX: Double = 1.0, factorY: Double = 1.0, firstX: Double = 0.0, lastX: Double = 0.0) {
        this.dataString = data
        this.parser = Parser()
        this.factorX = factorX
        this.factorY = factorY
        this.firstX = firstX
        this.lastX = lastX

        val trimmedFormat = dataFormat.replace(" ", "")
        when (trimmedFormat) {
            "(X++(Y..Y))", "(X++(R..R))", "(X++(I..I))" -> parsePseudoData()
            else -> parseXYData()
        }
    }

    private fun parsePseudoData() {
        val arrStartX: ArrayList<Double> = arrayListOf()

        val dataLines = dataString.split("\n")

        var nPoints = 0.0
        var isSkipCheckPoint = false
        for ((lineIdx, line) in dataLines.withIndex()) {
            val parsedLine = parser.parse(line)
            val parsedData = parsedLine.first
            val parsedDataCount = parsedData.size
            if (parsedDataCount > 1) {
                arrStartX.add(parsedData[0])
                var arrY: Array<Double> = arrayOf()
                if (!isSkipCheckPoint) {
                    arrY = parsedData.sliceArray(1..(parsedDataCount-1))
                }
                else {
                    val prevLine = listY[lineIdx - 1]
                    prevLine.removeLast()
                    listY[lineIdx-1] = prevLine
                    nPoints -= 1
                    arrY = parsedData.sliceArray(1..(parsedDataCount-1))
                }

                val arrYAsList = ArrayList(arrY.toList())
                listY.add(arrYAsList)
                nPoints += arrY.size

                isSkipCheckPoint = parsedLine.second
            }
        }

        val deltaX = (lastX - firstX) / (nPoints - 1)

        for ((idx, startX) in arrStartX.withIndex()) {
            var realXValue = startX * factorX
            val arrX: ArrayList<Double> = arrayListOf(realXValue)
            val arrY = listY[idx]
            val arrCount = arrY.size
            if (arrCount > 2) {
                for (i in 1 until arrY.size) {
                    realXValue += deltaX
                    arrX.add(realXValue)
                }
            }

            listX.add(arrX)
        }
    }

    private fun parseXYData() {
        val dataLines = dataString.split("\n")

        for (line in dataLines) {
            val removedSpaceLine = line.replace(" ", "")
            val arrXY = removedSpaceLine.split(";")
            val arrX: ArrayList<Double> = arrayListOf()
            val arrY: ArrayList<Double> = arrayListOf()
            for (xy in arrXY) {
                val values = xy.split(",")
                if (values.size < 2) {
                    return
                }
                val xValue = values[0].toDoubleOrNull()
                val yValue = values[1].toDoubleOrNull()
                if (xValue == null || yValue == null) {
                    return
                }

                arrX.add(xValue)
                arrY.add(yValue)
            }
            listX.add(arrX)
            listY.add(arrY)
        }
    }

    fun getListX(): Array<Double> {
        val result: ArrayList<Double> = arrayListOf()
        for (line in listX) {
            for (xValue in line) {
                result.add(xValue)
            }
        }
        return result.toTypedArray()
    }

    fun getListY(): Array<Double> {
        val result: ArrayList<Double> = arrayListOf()
        for (line in listY) {
            for (yValue in line) {
                val realY = yValue * factorY
                result.add(realY)
            }
        }
        return result.toTypedArray()
    }
}