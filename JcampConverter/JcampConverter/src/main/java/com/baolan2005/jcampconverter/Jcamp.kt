package com.baolan2005.jcampconverter

import android.util.Log
import com.baolan2005.jcampconverter.utils.isNumberic
import java.lang.Exception
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Jcamp {
    private val arrTitleData = arrayOf("xydata", "peaktable", "peak table", "xypoints", "data table")
    private val arrTypeDataXPlusY = arrayOf("(X++(Y..Y)", "(X++(R..R)", "(X++(I..I)")
    private val arrTypeDataXY = arrayOf("(XY..XY)")

    lateinit var children: ArrayList<Jcamp>

    public class Spectra {
        public var xValues: ArrayList<Double>
        public var yValues: ArrayList<Double>
        public var isReal = true

        constructor(xValues: ArrayList<Double>, yValues: ArrayList<Double>, isReal: Boolean) {
            this.xValues = xValues
            this.yValues = yValues
            this.isReal = isReal
        }

    }

    var spectra: ArrayList<Spectra> = ArrayList()
    public var dicData: HashMap<String, Any> = HashMap()

    public fun hasChild(): Boolean {
        if (children != null) {
            return children.size > 0
        }
        return false
    }

    constructor(originData: ArrayList<Any>) {
        var arrStartOfX = ArrayList<Double>()
        var arrNumberOfX = ArrayList<Int>()

        var isReadingData = false
        var dataType = ""
        var userDefineValue = ""
        var isReadingUserDefine = false

        var arrX = ArrayList<Double>()
        var arrY = ArrayList<Double>()
        var prevHasLastDIF = false
        var isRealData = true

        for (value in originData) {
            if (value is Array) {
                val childData = arrayListOf<Any>(value)
                val childJcamp = Jcamp(childData)
                if (this.children == null) {
                    this.children = ArrayList()
                }
                this.children.add(childJcamp)
            }
            else if (value is String) {
                val childString = value
                if (!(childString.startsWith("$$") || childString.startsWith("##"))) {
                    if (isReadingData) {
                        var dataString = childString
                        dataString = dataString.replace("$$ checkpoint", "")


                        var dataClass = ""
                        if (this.dicData.containsKey("##DATA CLASS")) {
                            val dClass = this.dicData["##DATA CLASS"]
                            if (dClass is String) {
                                dataClass = dClass
                                dataClass = dataClass.trim()
                            }
                        }

                        val filterTypeXPlusY = arrTypeDataXPlusY.filter {
                            dataType.contains(it)
                        }

                        if (filterTypeXPlusY.isNotEmpty()) {
                            // reading (X++(Y..Y) data
                            var firstX = 0.0
                            var lastX = 0.0
                            var npoints = 0.0
                            var xFactor = 1.0
                            var yFactor = 1.0

                            if (dataClass == "NTUPLES") {
                                //check ntuples data
                                var arrFirst: List<String>
                                var arrLast: List<String>
                                var arrFactor: List<String>

                                var varDim: String? = this.dicData["##VAR_DIM"] as String?
                                if (varDim.isNullOrEmpty()) {
                                    return
                                }
                                varDim = varDim.replace(" ", "")
                                varDim = varDim.trim()
                                val arrNPoints = varDim.split(",")

                                var first: String? = this.dicData["##FIRST"] as String?
                                if (first.isNullOrEmpty()) {
                                    first = first!!.replace(" ", "")
                                    first = first!!.trim()
                                    arrFirst = first!!.split(",")
                                    if (arrFirst.isNotEmpty()) {
                                        val first = arrFirst[0]
                                        firstX = first.toDouble()
                                    }
                                }

                                var last: String? = this.dicData["##LAST"] as String?
                                if (last.isNullOrEmpty()) {
                                    last = last!!.replace(" ", "")
                                    last = last!!.trim()
                                    arrLast = last!!.split(",")
                                    if (arrLast.isNotEmpty()) {
                                        val last = arrLast[0]
                                        lastX = last.toDouble()
                                    }
                                }

                                var factor: String? = this.dicData["##FACTOR"] as String?
                                if (factor.isNullOrEmpty()) {
                                    factor = factor!!.replace(" ", "")
                                    factor = factor!!.trim()
                                    arrFactor = factor!!.split(",")

                                    if (arrFactor.isNotEmpty()) {
                                        val factor = arrFactor[0]
                                        xFactor = factor.toDouble()
                                    }

                                    if (arrFactor.size > 1) {
                                        if (dataType.contains("(X++(R..R))")) {
                                            val factor = arrFactor[1]
                                            yFactor = factor.toDouble()
                                        }
                                        else {
                                            val factor = arrFactor[2]
                                            yFactor = factor.toDouble()
                                        }
                                    }
                                }

                                if (arrNPoints.isNotEmpty()) {
                                    val point = arrNPoints[0]
                                    npoints = point.toDouble()
                                }
                            }
                            else {
                                val first = this.dicData["##FIRSTX"]
                                if (first is String) {
                                    firstX = first.toDouble()
                                }
                                val last = this.dicData["##LASTX"]
                                if (last is String) {
                                    lastX = last.toDouble()
                                }
                                val point = this.dicData["##NPOINTS"]
                                if (point is String) {
                                    npoints = point.toDouble()
                                }
                                var factor = this.dicData["##XFACTOR"]
                                if (factor is String) {
                                    xFactor = factor.toDouble()
                                }
                                factor = this.dicData["##YFACTOR"]
                                if (factor is String) {
                                    yFactor = factor.toDouble()
                                }
                            }

                            val parsedData = parser(dataString)
                            var parsedValues = parsedData.first
                            val count = parsedValues.size
                            if (count > 0) {
                                val delta = (lastX-firstX)/npoints
                                var x = parsedValues[0]
                                x *= xFactor
                                arrX.add(x)

                                arrStartOfX.add(x)
                                arrNumberOfX.add(count-1)

                                val lastDIF = parsedData.second
                                if (arrStartOfX.size > 1 && lastDIF) {
                                    parsedValues.removeLast()
                                }
                                else if (prevHasLastDIF && parsedValues.size == 2 && !lastDIF) {
                                    //do not add y in last line check point and use the last x
                                    parsedValues.removeLast()
                                    arrX.removeAt(arrX.size-2)
                                }
                                //check previous is DIF form
                                prevHasLastDIF = lastDIF

                                for (i in 1 until parsedValues.size) {
                                    var y = parsedValues[i]
                                    y *= yFactor
                                    arrY.add(y)
                                    if (i > 1) {
                                        x += delta
                                        arrX.add(x)
                                    }
                                }
                            }
                        }
                        else {
                            //other types
                            val filterTypeXY = arrTypeDataXY.filter {
                                dataType.contains(it)
                            }
                            if (filterTypeXY.isNotEmpty()) {
                                // reading (XY..XY) data
                                dataString = dataString.replace(",", "")
                                dataString = dataString.replace(";", "")
                                val parsedData = dataString.split(" ")
                                for ((idx, value) in parsedData.withIndex()) {
                                    if (value is String) {
                                        val doubleVal = value.toDouble()
                                        if (idx%2 == 0) {
                                            arrX.add(doubleVal)
                                        }
                                        else {
                                            arrY.add(doubleVal)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (isReadingUserDefine) {
                        //reading user defined values
                        var userDefinedArrData: ArrayList<String>? = this.dicData[userDefineValue] as? ArrayList<String>?
                        if (userDefinedArrData == null) {
                            userDefinedArrData = ArrayList()
                        }

                        userDefinedArrData.add(childString)
                        this.dicData[userDefineValue] = userDefinedArrData
                    }
                }
                else {
                    val arrFilter = arrTitleData.filter {
                        childString.lowercase().contains(it)
                    }
                    isReadingData = arrFilter.isNotEmpty()

                    //check data type if start reading
                    dataType = ""
                    if (isReadingData) {
                        dataType = childString
                    }
                    else if (arrX.isNotEmpty()) {
                        val xValues = arrX.clone() as ArrayList<Double>
                        val yValues = arrY.clone() as ArrayList<Double>
                        val spec = Spectra(xValues, yValues, isRealData)
                        this.spectra.add(spec)
                        arrX.clear()
                        arrY.clear()
                    }

                    val spittedData = childString.split("=")
                    var lhs = ""
                    var rhs = ""
                    if (spittedData.size > 1) {
                        lhs = spittedData[0]
                        rhs = spittedData[1]
                    }
                    else if (spittedData.isNotEmpty()) {
                        lhs = spittedData[0]
                    }

                    if (dataType.contains("(X++(I..I))")) {
                        isRealData = false
                    }
                    else if (dataType.contains("(X++(R..R))")) {
                        isRealData = true
                    }

                    this.dicData[lhs] = rhs

                    //check user defined value
                    isReadingUserDefine = false
                    userDefineValue = ""
                    if (lhs.startsWith("##$")) {
                        isReadingUserDefine = true
                        userDefineValue = lhs
                    }
                }
            }
        }
    }

    //region Parsing Jcamp Data
    private fun getDIFValue(difStr: Double, prev: Double = 0.0): Double {
        return difStr + prev
    }

    private fun scanner(strVal: String): ArrayList<String> {
        var result = ArrayList<String>()


        var tmpStr = ""
        val scanStr = strVal

        var startPoint = -1

        for ((idx, c) in scanStr.withIndex()) {
            if (idx == startPoint-1) {
                continue
            }
            if (c.isNumberic() || c.equals('.', true)) {
                tmpStr = tmpStr.plus(c)
            }
            else {
                if (tmpStr != "") {
                    tmpStr = tmpStr.trim()
                    result.add(tmpStr)
                    tmpStr = ""
                }

                val charString = c.toString()
                if (Constants.DUP.containsKey(charString)) {
                    var nextChars = ""
                    if (idx < scanStr.length - 1) {
                        startPoint = idx+1
                        while (scanStr[startPoint].isNumberic()) {
                            nextChars = nextChars.plus(scanStr[idx+1])
                            startPoint += 1
                        }
                    }
                    val dupVal: Int = Constants.DUP[charString]!!
                    val strDupValFull = "%d%s".format(Locale.getDefault(), dupVal, nextChars)
                    var dupValInt: Int = dupVal
                    try {
                        strDupValFull.toInt()
                    }
                    catch (e: Exception) {

                    }
                    //Check DUP
                    for (i in 0 until dupValInt-1) {
                        val lastTmpStr = result.last()
                        if (!lastTmpStr.isNullOrEmpty()) {
                            result.add(lastTmpStr)
                        }
                    }
                }
                else if (Constants.SQZ.containsKey(charString)) {
                    val sqzVal = Constants.SQZ[charString]
                    tmpStr = tmpStr.plus(sqzVal)
                }
                else {
                    tmpStr = tmpStr.plus(c)
                }
            }
        }

        if (tmpStr != "") {
            tmpStr = tmpStr.trim()
            result.add(tmpStr)
            tmpStr = ""
        }

        return result
    }

    private fun parser(strVal: String): Pair<ArrayList<Double>, Boolean> {
        var result = ArrayList<Double>()

        val scannedResult = scanner(strVal)
        var hasLastDIF = false

        for ((idx, value) in scannedResult.withIndex()) {
            if (value == "") {
                continue
            }
            try {
                val doubleVal = value.toDouble()
                result.add(doubleVal)
                hasLastDIF = false
            }
            catch (e: Exception) {
                // DIF value
                val firstChar = value[0].toString()
                val intFirst: String = Constants.DIF[firstChar]!!
                val subVal = value.subSequence(1, value.length)
                val difValStr = intFirst.plus(subVal)
                try {
                    val difVal = difValStr.toDouble()
                    var convertedDIFVal = 0.0
                    if (result.size > 0) {
                        val prev = result[result.size-1]
                        convertedDIFVal = getDIFValue(difVal, prev)
                    }
                    else {
                        convertedDIFVal = getDIFValue(difVal)
                    }
                    result.add(convertedDIFVal)
                    if (idx == scannedResult.size-1) {
                        hasLastDIF = true
                    }
                }
                catch (e: Exception) {

                }
            }
        }

        return Pair(result, hasLastDIF)
    }

    //endregion
}