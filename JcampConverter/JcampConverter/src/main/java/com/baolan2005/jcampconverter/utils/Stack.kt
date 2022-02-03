package com.baolan2005.jcampconverter

public fun <T> ArrayList<T>.push(item: T) {
    this.add(item)
}

public fun <T> ArrayList<T>.pop() : T {
    val last = this.last()
    if (last != null) {
        this.removeLast()
    }
    return last
}

public fun <T> ArrayList<T>.peak(): T {
    return this.last()
}
