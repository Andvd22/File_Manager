package com.example.mylearning.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mylearning.R
import com.example.mylearning.view.SettingActivity

class WidgetPage2Provider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_home_2)

        // Click vào bất kỳ đâu đều mở SettingActivity
        val pendingIntent = createPendingIntent(context, "SETTING", 0)

        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)
        views.setOnClickPendingIntent(R.id.searchBar, pendingIntent)
        views.setOnClickPendingIntent(R.id.fileItem1, pendingIntent)
        views.setOnClickPendingIntent(R.id.fileItem2, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun createPendingIntent(context: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, SettingActivity::class.java).apply {
            this.action = action
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
