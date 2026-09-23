package ir.almasglass.sales.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val IRAN_TZ: TimeZone = TimeZone.getTimeZone("Asia/Tehran")

private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

fun toPersianDigits(input: String): String {
    val sb = StringBuilder()
    for (c in input) {
        if (c in '0'..'9') sb.append(PERSIAN_DIGITS[c - '0']) else sb.append(c)
    }
    return sb.toString()
}

/** Converts a Gregorian (year, month 1-12, day) to Jalali (year, month 1-12, day). */
private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var gy2 = gy - 1600
    val gm2 = gm - 1
    val gd2 = gd - 1

    var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
    for (i in 0 until gm2) {
        gDayNo += gDaysInMonth[i]
    }
    if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0)) {
        gDayNo += 1
    }
    gDayNo += gd2

    var jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    jDayNo %= 12053
    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461
    if (jDayNo >= 366) {
        jy += (jDayNo - 1) / 365
        jDayNo = (jDayNo - 1) % 365
    }
    val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
    var i = 0
    var jm = 1
    while (i < 11 && jDayNo >= jDaysInMonth[i]) {
        jDayNo -= jDaysInMonth[i]
        i++
        jm++
    }
    val jd = jDayNo + 1
    return Triple(jy, jm, jd)
}

/** Formats the given epoch millis as Jalali date + Iran clock time, e.g. "۱۴۰۵/۰۷/۰۱ - ۱۴:۲۲". */
fun formatJalaliDateTime(epochMillis: Long): String {
    val cal = Calendar.getInstance(IRAN_TZ)
    cal.timeInMillis = epochMillis
    val (jy, jm, jd) = gregorianToJalali(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
    val timeFmt = SimpleDateFormat("HH:mm", Locale.US)
    timeFmt.timeZone = IRAN_TZ
    val timeStr = timeFmt.format(Date(epochMillis))
    val dateStr = "%04d/%02d/%02d".format(jy, jm, jd)
    return toPersianDigits("$dateStr - $timeStr")
}

fun nowMillis(): Long = System.currentTimeMillis()
