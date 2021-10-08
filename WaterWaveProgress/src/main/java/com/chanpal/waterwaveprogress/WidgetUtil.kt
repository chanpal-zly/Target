package com.chanpal.waterwaveprogress

import android.content.Context

object WidgetUtil {
    fun pp2Px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun px2Dp(context: Context, px: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }
}