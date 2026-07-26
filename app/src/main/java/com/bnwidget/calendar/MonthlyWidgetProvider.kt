package com.bnwidget.calendar

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.widget.RemoteViews
import java.util.Calendar

class MonthlyWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_PREV_MONTH = "com.bnwidget.calendar.ACTION_PREV_MONTH"
        const val ACTION_NEXT_MONTH = "com.bnwidget.calendar.ACTION_NEXT_MONTH"
        const val PREFS_NAME = "monthly_widget_prefs"

        fun offsetKey(appWidgetId: Int) = "offset_$appWidgetId"

        fun getOffset(context: Context, appWidgetId: Int): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(offsetKey(appWidgetId), 0)
        }

        fun setOffset(context: Context, appWidgetId: Int, offset: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(offsetKey(appWidgetId), offset).apply()
        }

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_monthly)

            val offset = getOffset(context, appWidgetId)
            val base = Calendar.getInstance()
            base.set(Calendar.DAY_OF_MONTH, 1)
            base.add(Calendar.MONTH, offset)

            val year = base.get(Calendar.YEAR)
            val month = base.get(Calendar.MONTH) // 0-based, matches JS convention

            views.setTextViewText(R.id.tv_month_year, "${BengaliDateUtils.bnMonthNames[month]} $year")

            // prev / next বাটনের PendingIntent
            views.setOnClickPendingIntent(R.id.btn_prev, buildActionPendingIntent(context, appWidgetId, ACTION_PREV_MONTH))
            views.setOnClickPendingIntent(R.id.btn_next, buildActionPendingIntent(context, appWidgetId, ACTION_NEXT_MONTH))

            fillGrid(context, views, year, month)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun buildActionPendingIntent(context: Context, appWidgetId: Int, action: String): PendingIntent {
            val intent = Intent(context, MonthlyWidgetProvider::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                // প্রতিটি widget/action এর জন্য আলাদা data URI, না হলে extras মিশে যেতে পারে
                data = Uri.parse("bnwidget://widget/$appWidgetId/$action")
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            return PendingIntent.getBroadcast(context, appWidgetId, intent, flags)
        }

        private fun cellId(context: Context, index: Int): Int {
            return context.resources.getIdentifier("cell_$index", "id", context.packageName)
        }

        private fun fillGrid(context: Context, views: RemoteViews, year: Int, month: Int) {
            val today = Calendar.getInstance()
            val isCurrentMonth = today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month
            val todayDay = today.get(Calendar.DAY_OF_MONTH)

            val firstOfMonth = Calendar.getInstance()
            firstOfMonth.clear()
            firstOfMonth.set(year, month, 1)
            val firstDayOfWeek = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun

            val daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

            var cellIndex = 0

            // মাসের আগের ফাঁকা ঘর
            for (i in 0 until firstDayOfWeek) {
                setEmptyCell(context, views, cellIndex)
                cellIndex++
            }

            for (d in 1..daysInMonth) {
                if (cellIndex >= 42) break
                val dateCal = Calendar.getInstance()
                dateCal.clear()
                dateCal.set(year, month, d)
                val isSunday = dateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                val isToday = isCurrentMonth && d == todayDay
                val bnInfo = BengaliDateUtils.getBengaliInfo(dateCal)
                val evs = BengaliDateUtils.getEvents(year, month, d)

                setDayCell(context, views, cellIndex, d, bnInfo, isSunday, isToday, evs)
                cellIndex++
            }

            // মাসের পরের ফাঁকা ঘর
            while (cellIndex < 42) {
                setEmptyCell(context, views, cellIndex)
                cellIndex++
            }
        }

        private fun setEmptyCell(context: Context, views: RemoteViews, index: Int) {
            val id = cellId(context, index)
            if (id == 0) return
            views.setInt(id, "setBackgroundResource", R.drawable.bg_cell_empty)
            views.setTextViewText(id, "")
        }

        private fun setDayCell(
            context: Context,
            views: RemoteViews,
            index: Int,
            day: Int,
            bnInfo: BengaliInfo,
            isSunday: Boolean,
            isToday: Boolean,
            evs: List<EventItem>
        ) {
            val id = cellId(context, index)
            if (id == 0) return

            val bg = if (isToday) R.drawable.bg_cell_today else R.drawable.bg_cell
            views.setInt(id, "setBackgroundResource", bg)

            val numberColor = if (isSunday) 0xFFCC0000.toInt() else 0xFF00008B.toInt()

            val sb = SpannableStringBuilder()
            val numStart = sb.length
            sb.append(day.toString())
            val numEnd = sb.length
            sb.append("\n")
            val subStart = sb.length
            sb.append(BengaliDateUtils.toBn(bnInfo.day))
            if (evs.isNotEmpty()) {
                sb.append(" \u25CF") // ছোট বিন্দু = ইভেন্ট আছে বোঝাতে
            }
            val subEnd = sb.length

            sb.setSpan(StyleSpan(Typeface.BOLD), numStart, numEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(RelativeSizeSpan(1.35f), numStart, numEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(ForegroundColorSpan(numberColor), numStart, numEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            sb.setSpan(RelativeSizeSpan(0.75f), subStart, subEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(ForegroundColorSpan(0xFF555555.toInt()), subStart, subEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (evs.isNotEmpty()) {
                val dotColor = when (evs[0].type) {
                    "festival" -> 0xFF9C27B0.toInt()
                    "birthday" -> 0xFFE91E8C.toInt()
                    "important" -> 0xFF1E88E5.toInt()
                    "bank" -> 0xFF00897B.toInt()
                    "gov" -> 0xFFE53935.toInt()
                    else -> 0xFF888888.toInt()
                }
                sb.setSpan(ForegroundColorSpan(dotColor), subEnd - 1, subEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            views.setTextViewText(id, sb)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val appWidgetManager = AppWidgetManager.getInstance(context)

        when (intent.action) {
            ACTION_PREV_MONTH -> {
                setOffset(context, appWidgetId, getOffset(context, appWidgetId) - 1)
                updateWidget(context, appWidgetManager, appWidgetId)
            }
            ACTION_NEXT_MONTH -> {
                setOffset(context, appWidgetId, getOffset(context, appWidgetId) + 1)
                updateWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        for (id in appWidgetIds) {
            editor.remove(offsetKey(id))
        }
        editor.apply()
    }
}
