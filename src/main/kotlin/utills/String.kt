package org.example.utills

fun String.formatCookingTime() = this.split(" ").filter { it != "+" }.chunked(2).map {
    if (it[1][0] == 'ч') 60 * it[0].toInt()
    else if (it[1][0] == 'с') 1440  * it[0].toInt()
    else it[0].toInt()
}.reduce { acc, i -> acc + i }