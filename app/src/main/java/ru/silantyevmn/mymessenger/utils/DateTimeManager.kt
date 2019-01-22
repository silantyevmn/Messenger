package ru.silantyevmn.mymessenger.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeManager{
    fun getTime(longTime: Long): String{
        val cal = Calendar.getInstance()
        cal.setTimeInMillis(longTime)
        return SimpleDateFormat("hh:mm").format(cal.time)
    }
}
