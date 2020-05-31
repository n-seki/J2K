package com.nseki.parser

import java.lang.StringBuilder

fun String.toCamelCase(): String {
    val splits = split("_")
    val buf = StringBuilder(splits[0])
    for (i in 1 until splits.size) {
        buf.append(splits[i].toUpperFirst())
    }
    return buf.toString()
}

fun String.toClassName(ignoreLastS: Boolean = false): String {
    val firstUpper = toCamelCase().toUpperFirst()
    return if (ignoreLastS && firstUpper.last() == 's') {
        firstUpper.dropLast(1)
    } else {
        firstUpper
    }
}

fun String.toUpperFirst(): String {
    if (this.isEmpty()) return this
    val first = first().toString()
    return replaceFirst(first, first.toUpperCase())
}