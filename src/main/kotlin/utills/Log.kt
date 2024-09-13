package org.example.utills

object Log {

    fun d(msg: String) = println(formatMessage(GREEN, msg))
    fun e(msg: String) = println(formatMessage(RED, msg))
    fun w(msg: String) = println(formatMessage(YELLOW, msg))
    fun i(msg: String) = println(formatMessage(BLUE, msg))

    private fun formatMessage(color: String, msg: String) = "$color $msg $color"

    private const val RED: String = "\u001b[0;31m"
    private const val YELLOW: String = "\u001b[0;33m"
    private const val BLUE: String = "\u001b[0;34m"
    private const val GREEN: String = "\u001B[32m"

}