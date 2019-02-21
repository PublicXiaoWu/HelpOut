package com.gkzxhn.helpout.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 方 on 2018/3/6.
 */
object DateUtil {

    val PATTERN_yyyyMMdd_HHmm = "yyyy-MM-dd HH:mm"
    val PATTERN_yyyyMM = "yyyy-MM"
    val PATTERN_CN_yyyyMM = "yyyy年MM月"
    val PATTERN_DOT_yyyyMMdd_HHmm = "yyyy.MM.dd HH:mm"
    val PATTERNyyyy_MM_dd= "yyyy/MM/dd"
    val PATTERN_DOT_yyyyMMdd = "yyyy.MM.dd"
    val PATTERN_yyyyMMdd = "yyyy-MM-dd"
    val PATTERN_CN_MMdd_HHmm = "MM月dd日 HH:mm"

    val DAY_MILLIS = 24 * 3600 * 1000

    val HOUR_MILLIS = 3600 * 1000
        val HOUR_SECONDS = 3600

    val MINUTE_MILLIS = 60 * 1000
    val MINUTE_SECONDS = 60

    val SECOND_MILLIS = 1000
    //MeetingsInfo(code=200, meetings=[2018-03-08 9:00 - 9:30, 2018-03-12 9:00 - 9:30])

    /**
     * 获取当前的date
     */
    fun getTime() = Date(System.currentTimeMillis())

    /**
     * 得到dateStr毫秒值
     */
    fun getMillsFromStr(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long{
        val date = getDateFromStr(dateStr, pattern)?: Date()
        return date.time
    }

    private fun getDateFromStr(dateStr: String, pattern: String): Date? {
        var date: Date? = null
        try {
            date = SimpleDateFormat(pattern).parse(dateStr)
        }catch (e: Exception) {
            Log.e(DateUtil::class.java.simpleName, e.message)
        }
        return date
    }

    /**
     * 格式化millis
     */
    fun formatDateFromMillis(mills: Long, pattern: String = PATTERN_CN_MMdd_HHmm): String {
        var dateStr = ""
        try {
            dateStr = SimpleDateFormat(pattern).format(Date(mills))
        }catch (e: Exception) {
            Log.e(DateUtil::class.java.simpleName, e.message)
        }
        return dateStr
    }

    fun changePatternFromStr(dateStr: String,
                             patternBefore: String = PATTERN_yyyyMMdd_HHmm,
                             patternAfter: String = PATTERN_CN_MMdd_HHmm): String? {
        val dateFromStr = getDateFromStr(dateStr, patternBefore)
        var formatStr: String? = null
        try {
            formatStr = SimpleDateFormat(patternAfter).format(dateFromStr)
        }catch (e: Exception) {
            Log.e(DateUtil::class.java.simpleName, e.message)
        }
        return formatStr
    }

    /**
     * 得到现在距离dateStr的毫秒值
     */
    fun getMills2Now(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long {
        var millis = 0L
        try {
            millis = getMillsFromStr(dateStr, pattern) - System.currentTimeMillis()
        }catch (e: Exception) {
            Log.e(DateUtil::class.java.simpleName, e.message)
        }
        return millis
    }

    /**
     * 距dateStr还有多少天
     */
    fun getDay2Now(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long {
        val millis = getMills2Now(dateStr, pattern)
        if (millis> 0) {
            return millis / DAY_MILLIS + 1
        }else {
            return millis / DAY_MILLIS
        }
    }

    fun getHours2Now(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long {
        val millis = getMills2Now(dateStr, pattern) % DAY_MILLIS
        return millis / HOUR_MILLIS
    }

    fun getMinutes2Now(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long {
        val millis = getMills2Now(dateStr, pattern) % HOUR_MILLIS
        return millis / MINUTE_MILLIS
    }

    fun getSecond2Now(dateStr: String, pattern: String = PATTERN_yyyyMMdd_HHmm): Long {
        val mills = getMills2Now(dateStr, pattern) % MINUTE_MILLIS
        return mills / SECOND_MILLIS
    }
}