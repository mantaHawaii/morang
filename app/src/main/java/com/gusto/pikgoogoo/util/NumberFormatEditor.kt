package com.gusto.pikgoogoo.util

import java.text.DecimalFormat

class NumberFormatEditor {

    fun transfromNumber(number : Double): String {
        when {
            number < 1.0 -> return DecimalFormat("##.#").format(number)
            number < 10.0 -> return DecimalFormat("##.#").format(number)
            number < 1000.0 -> return DecimalFormat("####").format(number)
            number < 10000.0 -> {
                val editedNumber = number/1000.0
                return DecimalFormat("#.#천").format(editedNumber)
            }
            number < 1000000.0 -> {
                val editedNumber = number/10000.0
                return DecimalFormat("#.#만").format(editedNumber)
            }
            number < 1000000000.0 -> {
                val editedNumber = number/100000000.0
                return DecimalFormat("#.#백만").format(editedNumber)
            }
            else -> return DecimalFormat("###,###.##").format(number)
        }
    }

    fun transfromNumber(number : Int): String {
        when {
            number < 1000 -> return DecimalFormat("####").format(number)
            number < 10000 -> {
                val editedNumber = number/1000.0
                return DecimalFormat("#.#천").format(editedNumber)
            }
            number < 1000000 -> {
                val editedNumber = number/10000.0
                return DecimalFormat("#.#만").format(editedNumber)
            }
            number < 1000000000 -> {
                val editedNumber = number/100000000.0
                return DecimalFormat("#.#백만").format(editedNumber)
            }
            else -> return DecimalFormat("###,###.##").format(number)
        }
    }

}