package com.example.mylearning.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mylearning.R
import com.example.mylearning.view.FeedbackActivity

class WidgetPage3Provider : AppWidgetProvider() {

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
        val views = RemoteViews(context.packageName, R.layout.widget_home_3)

        // Click vào bất kỳ đâu đều mở FeedbackActivity
        val pendingIntent = createPendingIntent(context, "FEEDBACK", 0)

        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnRecent, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnFavorite, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnPdf, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnWord, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnPpt, pendingIntent)
        views.setOnClickPendingIntent(R.id.btnExcel, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun createPendingIntent(context: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, FeedbackActivity::class.java).apply {
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
