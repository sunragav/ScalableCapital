package com.sunragav.scalablecapital.core.util

import com.sunragav.scalablecapital.core.util.RangeConstraint.END
import com.sunragav.scalablecapital.core.util.RangeConstraint.START
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.ENGLISH

enum class RangeConstraint {
    START, END
}

data class DateRangeForMonth(val month: String, val dateRange: Pair<String, String>)

class DateRange(private val month: Date) {
    fun getDateRangeForMonth(month: String): DateRangeForMonth {
        return DateRangeForMonth(
            month, Pair(
                dateFormatter.format(getDate(START)),
                dateFormatter.format(getDate(END))
            )
        )
    }

    private fun getDate(rangeConstraint: RangeConstraint) = GregorianCalendar.getInstance().apply {
        time = month
        set(
            Calendar.DAY_OF_MONTH,
            when (rangeConstraint) {
                START -> {
                    getActualMinimum(Calendar.DAY_OF_MONTH)
                }
                else -> getActualMaximum(Calendar.DAY_OF_MONTH)
            }
        )
        when (rangeConstraint) {
            START -> {
                setTimeToBeginningOfDay()
            }
            else -> setTimeToEndOfDay()
        }
    }.time

    private fun Calendar.setTimeToBeginningOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun Calendar.setTimeToEndOfDay() {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    companion object {
        private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        private const val TIME = "T00:00:00Z"

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", ENGLISH)
        private val monthFormat = SimpleDateFormat("MMMM", ENGLISH)

        /***
         *
         * @param createdYear year as 4 digits integer
         * @param startMonth month (between 1 to 12) as integer
         * @param endMonth month (between 1 to 12) as integer
         */
        fun getDateRangesForYear(createdYear: Int, startMonth: Int, endMonth: Int) =
            (startMonth..endMonth)
                .map { it.toString().padStart(2, '0') }
                .mapNotNull { month ->
                    SimpleDateFormat(
                        DATE_TIME_PATTERN,
                        ENGLISH
                    ).parse("$createdYear-$month-01$TIME")?.let { date ->
                        DateRange(date).getDateRangeForMonth(getMonthName(month = month.toInt()))
                    }
                }

        private fun getMonthName(month: Int): String {
            val calendar = GregorianCalendar()
            calendar[Calendar.DAY_OF_MONTH] = 1
            calendar[Calendar.MONTH] = month - 1
            return monthFormat.format(calendar.time)
        }
    }
}