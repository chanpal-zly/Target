package com.chanpal.waterwaveprogress

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet

class WaterWaveAttrInit @SuppressLint("Recycle") constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) {
    private val progressWidth // 进度条宽度
            : Int
    private val progressColor: Int
    private val progressBgColor: Int
    private val waterWaveColor: Int
    private val waterWaveBgColor: Int
    private val progress2WaterWidth // 进度条和水波之间的间距
            : Int
    private val isShowProgress // 是否显示进度条
            : Boolean
    private val isShowNumerical // 是否显示百分比
            : Boolean
    private val fontSize: Int
    private val textColor: Int
    private val progress: Int
    private val maxProgress: Int

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.WaterWaveProgress, defStyle, 0
        )
        progressWidth = typedArray.getDimensionPixelOffset(
            R.styleable.WaterWaveProgress_progressWidth, 0
        )
        progressColor = typedArray.getColor(
            R.styleable.WaterWaveProgress_progressColor, -0xcc4a1b
        )
        progressBgColor = typedArray.getColor(
            R.styleable.WaterWaveProgress_progressBgColor, -0x414142
        )
        waterWaveColor = typedArray.getColor(
            R.styleable.WaterWaveProgress_waterWaveColor, -0xb44202
        )
        waterWaveBgColor = typedArray.getColor(
            R.styleable.WaterWaveProgress_waterWaveBgColor, -0x222223
        )
        progress2WaterWidth = typedArray.getDimensionPixelOffset(
            R.styleable.WaterWaveProgress_progress2WaterWidth, 0
        )
        isShowProgress = typedArray.getBoolean(
            R.styleable.WaterWaveProgress_showProgress, true
        )
        isShowNumerical = typedArray.getBoolean(
            R.styleable.WaterWaveProgress_showNumerical, true
        )
        fontSize = typedArray.getDimensionPixelOffset(
            R.styleable.WaterWaveProgress_fontSize, 0
        )
        textColor = typedArray.getColor(
            R.styleable.WaterWaveProgress_textColor, -0x1
        )
        progress = typedArray.getInteger(
            R.styleable.WaterWaveProgress_progress, 15
        )
        maxProgress = typedArray.getInteger(
            R.styleable.WaterWaveProgress_maxProgress, 100
        )
        typedArray.recycle()
    }

    fun getProgressWidth(): Int {
        return progressWidth
    }

    fun getProgressColor(): Int {
        return progressColor
    }

    fun getProgressBgColor(): Int {
        return progressBgColor
    }

    fun getWaterWaveColor(): Int {
        return waterWaveColor
    }

    fun getWaterWaveBgColor(): Int {
        return waterWaveBgColor
    }

    fun getProgress2WaterWidth(): Int {
        return progress2WaterWidth
    }

    fun isShowProgress(): Boolean {
        return isShowProgress
    }

    fun isShowNumerical(): Boolean {
        return isShowNumerical
    }

    fun getFontSize(): Int {
        return fontSize
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun getProgress(): Int {
        return progress
    }

    fun getMaxProgress(): Int {
        return maxProgress
    }

}