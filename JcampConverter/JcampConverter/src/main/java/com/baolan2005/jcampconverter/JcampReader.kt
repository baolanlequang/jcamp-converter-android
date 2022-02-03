package com.baolan2005.jcampconverter

import java.io.File
import java.lang.Exception

class JcampReader {
    lateinit var jcamp: Jcamp

    constructor() {}

    constructor(filePath: String) {
        try {
            val data = File(filePath).readLines(Charsets.US_ASCII)
            val structuredData = parsingStructure(data)
            this.jcamp = Jcamp(structuredData)
        }
        catch (e: Exception) {

        }
    }

    private fun popChildBlock(inputStack: ArrayList<Any>, inputQueue: ArrayList<Any>): Pair<ArrayList<Any>, ArrayList<Any>> {
        var stack: ArrayList<Any> = inputStack.clone() as ArrayList<Any>
        var queue: ArrayList<Any> = inputQueue.clone() as ArrayList<Any>
        var topStack = stack.peak()
        while ((topStack is String) && (!topStack.isNullOrEmpty()) && !(topStack.startsWith("##TITLE="))) {
            val popValue = stack.pop()
            if ((popValue is String) && (!popValue.isNullOrEmpty())) {
                queue.add(0, popValue)
                topStack = stack.peak()
            }
        }

        val remainTop = stack.peak()
        if (remainTop is String && !remainTop.isNullOrEmpty() && remainTop.startsWith("##TITLE=")) {
            val popValue = stack.pop()
            if (popValue is String && !popValue.isNullOrEmpty()) {
                queue.add(0, popValue)
            }
        }

        return Pair(stack, queue)
    }

    private fun parsingStructure(data: List<String>): ArrayList<Any> {
        var stack = ArrayList<Any>()

        for (line in data) {
            val trimmedLine = line.trim()
            if (trimmedLine == "") {
                //ignore empty line
                continue
            }

            val remainTop = stack.peak()
            if (remainTop is String && remainTop.startsWith("##END=")) {
                val popChildren = popChildBlock(stack, ArrayList())
                stack = popChildren.first
                stack.push(popChildren.second)
                stack.push(trimmedLine)
            }
            else {
            stack.push(trimmedLine)
        }
        }

        return stack
    }
}