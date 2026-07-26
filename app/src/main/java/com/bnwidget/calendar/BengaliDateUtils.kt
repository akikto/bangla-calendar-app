package com.bnwidget.calendar

import java.util.Calendar

data class EventItem(val type: String, val title: String)
data class BengaliInfo(val day: Int, val monthName: String)

object BengaliDateUtils {

    val bnMonthNames = arrayOf(
        "জানুয়ারী", "ফেব্রুয়ারী", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    val bnCalMonths = arrayOf(
        "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
        "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
    )

    val weekdayNamesFull = arrayOf("রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার")

    // [গ্রেগরিয়ান মাস (0-based), তারিখ] যেদিন প্রতিটি বাংলা মাস শুরু হয় (২০২৬ এর হিসাবে)
    private val bnStarts = arrayOf(
        intArrayOf(3, 14), intArrayOf(4, 16), intArrayOf(5, 16), intArrayOf(6, 17),
        intArrayOf(7, 17), intArrayOf(8, 17), intArrayOf(9, 17), intArrayOf(10, 16),
        intArrayOf(11, 16), intArrayOf(0, 15), intArrayOf(1, 14), intArrayOf(2, 15)
    )

    fun toBn(n: Int): String {
        val d = arrayOf("০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")
        return n.toString().map { c -> if (c.isDigit()) d[c - '0'] else c.toString() }.joinToString("")
    }

    private fun makeStart(idx: Int, baseYear: Int): Calendar {
        val (m, day) = bnStarts[idx]
        val yr = if (idx >= 9) baseYear + 1 else baseYear
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(yr, m, day)
        return cal
    }

    fun getBengaliInfo(cal: Calendar): BengaliInfo {
        val y = cal.get(Calendar.YEAR)
        for (base in y downTo y - 1) {
            for (i in 0 until 12) {
                val start = makeStart(i, base)
                val end = makeStart((i + 1) % 12, if (i == 11) base + 1 else base)
                if (!cal.before(start) && cal.before(end)) {
                    val dayCount = ((cal.timeInMillis - start.timeInMillis) / 86400000L).toInt() + 1
                    return BengaliInfo(dayCount, bnCalMonths[i])
                }
            }
        }
        return BengaliInfo(1, "")
    }


    val events: Map<String, List<EventItem>> = mapOf(
        // জানুয়ারী
        "2026-0-1" to listOf(EventItem("gov", "নববর্ষ (New Year's Day)")),
        "2026-0-12" to listOf(EventItem("gov", "স্বামী বিবেকানন্দের জন্মজয়ন্তী")),
        "2026-0-22" to listOf(EventItem("gov", "সরস্বতী পূজার আগের দিন")),
        "2026-0-23" to listOf(
            EventItem("gov", "নেতাজির জন্মজয়ন্তী"),
            EventItem("festival", "সরস্বতী পূজা (শ্রীপঞ্চমী)")
        ),
        "2026-0-26" to listOf(EventItem("gov", "প্রজাতন্ত্র দিবস (Republic Day)")),

        // ফেব্রুয়ারী
        "2026-1-4" to listOf(EventItem("gov", "শবে বরাত")),
        "2026-1-14" to listOf(EventItem("gov", "ঠাকুর পঞ্চানন বর্মার জন্মজয়ন্তী")),
        "2026-1-15" to listOf(EventItem("festival", "শিবরাত্রি")),

        // মার্চ
        "2026-2-3" to listOf(EventItem("festival", "দোলযাত্রা")),
        "2026-2-4" to listOf(EventItem("festival", "হোলি (দোলের পরের দিন)")),
        "2026-2-17" to listOf(EventItem("gov", "শ্রীশ্রী হরিচাঁদ ঠাকুরের জন্মদিন")),
        "2026-2-20" to listOf(EventItem("gov", "ইদ-উল-ফিতরের আগের দিন")),
        "2026-2-21" to listOf(EventItem("gov", "ইদ-উল-ফিতর")),
        "2026-2-28" to listOf(EventItem("festival", "রামনবমী")),
        "2026-2-31" to listOf(EventItem("gov", "মহাবীর জয়ন্তী")),

        // এপ্রিল
        "2026-3-1" to listOf(EventItem("bank", "ব্যাংক হিসাব বছর সমাপ্তি (শুধু ব্যাংকের ছুটি)")),
        "2026-3-3" to listOf(EventItem("gov", "গুড ফ্রাইডে")),
        "2026-3-4" to listOf(EventItem("festival", "ইস্টার শনিবার (খ্রিস্টানদের জন্য)")),
        "2026-3-14" to listOf(EventItem("gov", "ড. বি. আর. আম্বেদকরের জন্মজয়ন্তী")),
        "2026-3-15" to listOf(EventItem("festival", "পয়লা বৈশাখ (নববর্ষ)")),

        // মে
        "2026-4-1" to listOf(
            EventItem("gov", "মে দিবস"),
            EventItem("festival", "বুদ্ধ পূর্ণিমা")
        ),
        "2026-4-9" to listOf(EventItem("festival", "রবীন্দ্রনাথের জন্মজয়ন্তী (রবীন্দ্র জয়ন্তী)")),
        "2026-4-26" to listOf(EventItem("gov", "বকরি ইদের আগের দিন")),
        "2026-4-27" to listOf(EventItem("gov", "ইদ-উজ-জোহা (বকরি ইদ)")),

        // জুন
        "2026-5-26" to listOf(EventItem("gov", "মহরম")),
        "2026-5-30" to listOf(EventItem("festival", "হুল দিবস (আদিবাসী সাঁওতালদের জন্য)")),

        // জুলাই
        "2026-6-13" to listOf(EventItem("gov", "কবি ভানুভক্তের জন্মদিন (দার্জিলিং ও কালিম্পং)")),
        "2026-6-16" to listOf(EventItem("festival", "রথযাত্রা")),

        // আগস্ট
        "2026-7-15" to listOf(EventItem("gov", "স্বাধীনতা দিবস (Independence Day)")),
        "2026-7-26" to listOf(EventItem("gov", "ফতেহা-দ্বাজ-দহম")),
        "2026-7-28" to listOf(EventItem("festival", "রাখী বন্ধন")),

        // সেপ্টেম্বর
        "2026-8-4" to listOf(EventItem("festival", "জন্মাষ্টমী")),
        "2026-8-17" to listOf(EventItem("festival", "বিশ্বকর্মা পূজা")),

        // অক্টোবর - দুর্গাপূজা
        "2026-9-2" to listOf(EventItem("gov", "গান্ধী জয়ন্তী")),
        "2026-9-10" to listOf(EventItem("festival", "মহালয়া")),
        "2026-9-15" to listOf(EventItem("festival", "মহাচতুর্থী")),
        "2026-9-16" to listOf(EventItem("festival", "মহাপঞ্চমী")),
        "2026-9-17" to listOf(EventItem("festival", "মহাষষ্ঠী")),
        "2026-9-18" to listOf(EventItem("festival", "মহাসপ্তমী")),
        "2026-9-19" to listOf(EventItem("festival", "মহাষ্টমী")),
        "2026-9-20" to listOf(EventItem("festival", "মহানবমী")),
        "2026-9-21" to listOf(EventItem("festival", "বিজয়া দশমী")),
        "2026-9-22" to listOf(EventItem("gov", "দুর্গাপূজার অতিরিক্ত ছুটি")),
        "2026-9-23" to listOf(EventItem("gov", "দুর্গাপূজার অতিরিক্ত ছুটি")),
        "2026-9-24" to listOf(EventItem("gov", "দুর্গাপূজার অতিরিক্ত ছুটি")),
        "2026-9-25" to listOf(EventItem("festival", "লক্ষ্মী পূজা")),
        "2026-9-26" to listOf(EventItem("festival", "লক্ষ্মী পূজার অতিরিক্ত ছুটি")),

        // নভেম্বর
        "2026-10-8" to listOf(EventItem("festival", "কালীপূজা / দীপাবলি")),
        "2026-10-9" to listOf(EventItem("gov", "কালীপূজার অতিরিক্ত ছুটি")),
        "2026-10-10" to listOf(EventItem("gov", "কালীপূজার অতিরিক্ত ছুটি")),
        "2026-10-11" to listOf(EventItem("festival", "ভ্রাতৃদ্বিতীয়া (ভাইফোঁটা)")),
        "2026-10-12" to listOf(EventItem("gov", "ভ্রাতৃদ্বিতীয়ার পরের দিন")),
        "2026-10-15" to listOf(
            EventItem("festival", "ছট পূজা"),
            EventItem("gov", "বীরসা মুন্ডার জন্মজয়ন্তী")
        ),
        "2026-10-16" to listOf(EventItem("gov", "ছট পূজার অতিরিক্ত ছুটি")),
        "2026-10-24" to listOf(EventItem("gov", "গুরু নানকের জন্মজয়ন্তী")),

        // ডিসেম্বর
        "2026-11-25" to listOf(EventItem("festival", "বড়দিন (Christmas)")),

        // ব্যক্তিগত উদাহরণ
        "2026-6-6" to listOf(EventItem("birthday", "উদাহরণ: রহিমের জন্মদিন"))
    )

    fun getEvents(year: Int, month: Int, day: Int): List<EventItem> {
        return events["$year-$month-$day"] ?: emptyList()
    }
}
