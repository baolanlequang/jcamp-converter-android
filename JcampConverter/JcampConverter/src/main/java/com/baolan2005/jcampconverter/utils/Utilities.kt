package com.baolan2005.jcampconverter.utils

public fun String.isNumeric(): Boolean {
    if (!this.isNullOrEmpty()) {
        val numsSet = setOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val thisSet = this.toSet()
        return numsSet.containsAll(thisSet)
    }
    return false
}

public fun Char.isNumberic(): Boolean {
    val numsSet = setOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    return numsSet.contains(this)
}

