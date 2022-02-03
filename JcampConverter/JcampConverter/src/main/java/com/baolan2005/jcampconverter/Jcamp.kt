package com.baolan2005.jcampconverter

import com.baolan2005.jcampconverter.utils.isNumberic
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Jcamp {
    private val arrTitleData = arrayOf("xydata", "peaktable", "peak table", "xypoints", "data table")
    private val arrTypeDataXPlusY = arrayOf("(X++(Y..Y)", "(X++(R..R)", "(X++(I..I)")
    private val arrTypeDataXY = arrayOf("(XY..XY)")

    lateinit var children: ArrayList<Jcamp>

    public class Spectra {
        public var xValues: ArrayList<Double> = ArrayList()
        public var yValues: ArrayList<Double> = ArrayList()
        public var isReal = true
    }

    public var spectra: ArrayList<Spectra> = ArrayList()
    public var dicData: HashMap<String, Any> = HashMap()

    public fun hasChild(): Boolean {
        if (children != null) {
            return children.size > 0
        }
        return false
    }

    init { }

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
                tmpStr.plus(c)
            }
            else {
                if (!tmpStr.equals("")) {
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
                            nextChars.plus(scanStr[idx+1])
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
                    tmpStr.plus(sqzVal)
                }
                else {
                    tmpStr.plus(c)
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
            try {
                val doubleVal = value.toDouble()
                result.add(doubleVal)
                hasLastDIF = false
            }
            catch (e: Exception) {
                // DIF value
                val firstChar = value[0].toString()
                val intFirst: String = Constants.DIF[firstChar]!!
                val subVal = value.subSequence(1, value.length-1)
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