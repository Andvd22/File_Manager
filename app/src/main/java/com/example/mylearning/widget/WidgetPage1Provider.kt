package com.example.mylearning.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mylearning.R
import com.example.mylearning.view.MainActivity

class WidgetPage1Provider : AppWidgetProvider() {

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
        val views = RemoteViews(context.packageName, R.layout.widget_home_1)

        // Click search bar
        views.setOnClickPendingIntent(
            R.id.searchBar,
            createPendingIntent(context, "SEARCH", 0)
        )

        // Click Trang chủ
        views.setOnClickPendingIntent(
            R.id.btnHome,
            createPendingIntent(context, "HOME", 1)
        )

        // Click Gần đây
        views.setOnClickPendingIntent(
            R.id.btnRecent,
            createPendingIntent(context, "RECENT", 2)
        )

        // Click Yêu thích
        views.setOnClickPendingIntent(
            R.id.btnFavorite,
            createPendingIntent(context, "FAVORITE", 3)
        )

        // Click Chỉnh sửa
        views.setOnClickPendingIntent(
            R.id.btnEdit,
            createPendingIntent(context, "EDIT", 4)
        )

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun createPendingIntent(context: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
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