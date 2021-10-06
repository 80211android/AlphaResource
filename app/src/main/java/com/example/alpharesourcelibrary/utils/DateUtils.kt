package com.movieplayer.android.utils

import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DateUtils private constructor(){

    companion object {
        val shared: DateUtils by lazy { DateUtils() }
    }

    /**
     * MM = Only month number
     * MMM = Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec (1st 3 letter of Month)
     * MMMM = Jaunary, February, March ..... (full month name)
     *
     * EEE = Sat, Sun, Mon ..... ( 1st 3 letters of day)
     * EEEE = Saturday, Sunday, Monday....(full day)
     *
     * HH = 24 hours format
     * hh = 12 hours format
     *
     * */

    fun formateDate(
        fromFormat: String = "yyyy-MM-dd HH:mm:ss", // 2020-04-22 22:40:12
        dateToFormat: String,
        toFormat: String = "dd MMM, yyyy, hh:mm:a" // 22 Apr, 2020 10:40 PM
    ): String {
        val inFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        var parsedDate: Date? = null
        try {
            parsedDate = inFormat.parse(dateToFormat)
        } catch (e: Exception) {
        }
        return if (parsedDate == null) "" else outFormat.format(parsedDate)
    }


    fun convertISOtimeToDate(
        dateToFormat: String,
        toFormat: String
    ): String {
        val isoFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        val inFormat = SimpleDateFormat(isoFormat, Locale.getDefault())
        inFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        var parsedDate: Date? = null
        try {
            parsedDate = inFormat.parse(dateToFormat)
        } catch (e: Exception) {
        }
        return if (parsedDate == null) "" else outFormat.format(parsedDate)
    }

    fun convertUnixTimeToDate(unixSeconds: Long, toFormat: String = "hh:mm a"): String {
        val date = Date(unixSeconds * 1000)
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())  // 12:00:00 pm
        return outFormat.format(date)
    }

    fun convertMilliSecondsToDate(milliSeconds: Long, toFormat: String = "hh:mm a"): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())  // 12:00:00 pm
        outFormat.timeZone = TimeZone.getTimeZone("UTC")
        return outFormat.format(calendar.timeInMillis)
    }

    fun convertToDateTimeArray(
        fromFormat: String = "dd-MM-yyyy HH:mm:ss",
        toDateFormat: String = "dd MMM, yyy", // 31 Jan, 2018
        toTimeFormat: String = "hh:mm a",  // 12:00:00 pm
        dateToFormat: String
    ): Array<String> {
        val inFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
        val outFormatDate = SimpleDateFormat(toDateFormat, Locale.getDefault())
        val outFormatTime = SimpleDateFormat(toTimeFormat, Locale.getDefault())
        var parsedDate: Date? = null
        try {
            parsedDate = inFormat.parse(dateToFormat)
        } catch (e: ParseException) {
        }
        return if (parsedDate != null)
            arrayOf(outFormatDate.format(parsedDate), outFormatTime.format(parsedDate))
        else
            arrayOf("", "")
    }

    fun getTodayName(toFormat: String = "EEE"): String { // EEE = Sat, Sun, Mon ...
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        return outFormat.format(Calendar.getInstance())
    }

    fun getCurrentMonthName(toFormat: String = "MMM"): String { // MMM = Jan, Feb ...
        val outFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        return outFormat.format(Calendar.getInstance())
    }
}