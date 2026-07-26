package com.bnwidget.calendar

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.util.Calendar

class DailyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_daily)

            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val monthIdx = cal.get(Calendar.MONTH) // 0-based
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val weekdayIdx = cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY=1 -> 0

            val bnInfo = BengaliDateUtils.getBengaliInfo(cal)

            views.setTextViewText(
                R.id.tv_greg_month_year,
                "${BengaliDateUtils.bnMonthNames[monthIdx]} $year"
            )
            views.setTextViewText(R.id.tv_day_number, day.toString())
            views.setTextViewText(
                R.id.tv_bn_date,
                "${BengaliDateUtils.toBn(bnInfo.day)} ${bnInfo.monthName}"
            )
            views.setTextViewText(R.id.tv_weekday, BengaliDateUtils.weekdayNamesFull[weekdayIdx])

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
