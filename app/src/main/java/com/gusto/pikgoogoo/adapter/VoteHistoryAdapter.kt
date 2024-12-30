package com.gusto.pikgoogoo.adapter

import android.util.Log
import com.gusto.pikgoogoo.data.GraphDTO
import com.gusto.pikgoogoo.data.VoteHistory
import java.text.SimpleDateFormat
import java.util.*

class VoteHistoryAdapter : Observable() {

    val generatedData: MutableList<GraphDTO> = mutableListOf()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    lateinit var startDate: Date
    lateinit var endDate: Date
    var startTms: Long = 0
    var endTms: Long = 0
    lateinit var startDateStr: String
    lateinit var endDateStr: String
    var lineColor = 0
    lateinit var startTime: String
    lateinit var endTime: String

    var avgVoteCount: Double = 0.0
    var allVoteCount: Int = 0

    var labelType: Int = 0
    val Y_SAME_M_SAME = 1
    val Y_DIFF_TERM_1_58 = 30
    val Y_DIFF_TERM_59 = 31
    val Y_SAME_M_DIFF_TERM_1_100 = 100
    val Y_SAME_M_DIFF_TERM_101 = 101

    init {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
    }

    fun setAll(list: List<VoteHistory>, sTime: String, eTime: String) {
        setStartEnd(sTime, eTime)
        setList(list)
    }

    fun setStartEnd(sTime: String, eTime: String) {

        Log.d("99_", sTime+" / "+eTime)

        startTime = sTime
        endTime = eTime

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

        startDate = sdf.parse(startTime)
        endDate = sdf.parse(endTime)

        cal.time = startDate
        val sYear: Int = cal.get(Calendar.YEAR)
        val sMonth: Int = cal.get(Calendar.MONTH)+1
        val sDay: Int = cal.get(Calendar.DAY_OF_MONTH)
        cal.time = endDate
        val eYear: Int = cal.get(Calendar.YEAR)
        val eMonth: Int = cal.get(Calendar.MONTH)+1
        val eDay: Int = cal.get(Calendar.DAY_OF_MONTH)

        val term = (((endDate.time-startDate.time)/(24*60*60*1000))+1).toInt()

        if (sYear !== eYear) {
            when (term) {
                0 -> {
                    startDateStr = ""
                    endDateStr = ""
                }
                in 1..58 -> {
                    startDateStr = sYear.toString()+"년 "+sMonth.toString()+"월"
                    endDateStr = sYear.toString()+"년 "+eMonth.toString()+"월"
                    labelType = Y_DIFF_TERM_1_58
                }
                else -> {
                    startDateStr = sYear.toString()+"년 "+sMonth.toString()+"월"
                    endDateStr = eYear.toString()+"년 "+eMonth.toString()+"월"
                    labelType = Y_DIFF_TERM_59
                }
            }
        } else {
            if (sMonth == eMonth) {
                startDateStr = sYear.toString()+"년 "+sMonth.toString()+"월"
                endDateStr = ""
                labelType = Y_SAME_M_SAME
            } else {
                when (term) {
                    0 -> {
                        startDateStr = ""
                        endDateStr = ""
                    }
                    in 1..100 -> {
                        startDateStr = sYear.toString()+"년 "+sMonth.toString()+"월"
                        endDateStr = eMonth.toString()+"월 "+eDay.toString()+"일"
                        labelType = Y_SAME_M_DIFF_TERM_1_100
                    }
                    else -> {
                        startDateStr = sYear.toString()+"년 "+sMonth.toString()+"월"
                        endDateStr = eMonth.toString()+"월"
                        labelType = Y_SAME_M_DIFF_TERM_101
                    }
                }
            }
        }
    }

    fun setList(list: List<VoteHistory>) {
        generatedData.clear()
        generatedData.addAll(generateDataForBinding(list))
        allVoteCount = 0
        for (item in generatedData) {
            allVoteCount += item.voteCount
        }
        avgVoteCount = allVoteCount.toDouble()/generatedData.size.toDouble()
        setChanged()
        notifyObservers()
    }

    private fun generateDataForBinding(list: List<VoteHistory>): List<GraphDTO> {

        val graphList = mutableListOf<GraphDTO>()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        cal.time = startDate

        Log.d("99_", "start: "+startDate.toString())
        Log.d("99_", "end: "+endDate.toString())

        var itemCount = 0

        while (cal.time <= endDate) {

            var label = ""

            Log.d("99_", "in while: "+cal.time.toString())

            when (labelType) {
                Y_SAME_M_SAME -> {
                    label = cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
                }
                Y_DIFF_TERM_1_58 -> {
                    label = (cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
                }
                Y_DIFF_TERM_59 -> {
                    label = cal.get(Calendar.YEAR).toString()+"년 "+(cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
                }
                Y_SAME_M_DIFF_TERM_1_100 -> {
                    label = (cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
                }
                Y_SAME_M_DIFF_TERM_101 -> {
                    label = (cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
                }
            }

            if (itemCount < list.size) {

                var item = list.get(itemCount)
                if (cal.time.time == item.dateMilliSeconds) {
                    graphList.add(GraphDTO(item.id, item.voteCount, item.dateMilliSeconds, label))
                    itemCount++
                } else {
                    graphList.add(GraphDTO(0, 0, cal.time.time, label))
                }

            } else {
                graphList.add(GraphDTO(0, 0, cal.time.time, label))
            }

            cal.add(Calendar.DATE, 1)

        }
        return graphList
    }

    fun timeToDate(time: Long): String {
        try {
            return sdf.format(time)
        } catch (e: Exception) {
            return e.toString()
        }
    }

}