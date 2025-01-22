package com.gusto.pikgoogoo.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class DateFormatManager {

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    private var timeNow: Date
    private val minute: Long
    private val hour: Long
    private val day: Long
    private val month: Long
    private val years: Long

    init {
        timeNow = calendar.time
        minute = 60*1000
        hour = minute*60
        day = hour*24
        month = day*30
        years = day*365
    }

    fun getTimeForm(timeString: String): String {

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString)
        val calculatedTime = (timeNow.time - date.time)

        when(calculatedTime) {

            in 0..minute-1 -> {
                return "방금 전"
            }
            in minute..hour-1 -> {
                return (calculatedTime/minute).toInt().toString()+"분 전"
            }
            in hour..day-1 -> {
                return (calculatedTime/hour).toInt().toString()+"시간 전"
            }
            in day..month-1 -> {
                return (calculatedTime/day).toInt().toString()+"일 전"
            }
            in month..years-1 -> {
                return (calculatedTime/month).toInt().toString()+"개월 전"
            }
            in years..Long.MAX_VALUE -> {
                return (calculatedTime/years).toInt().toString()+"년 전"
            }
            else -> {
                return "방금 전"
            }

        }


    }

}