package com.chanpal.waterwaveprogress

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.ref.WeakReference

class WaterWaveProgress : View {
    // 水的画笔 // 画圆环的画笔// 进度百分比的画笔
    private var mPaintWater: Paint? = null
    private var mRingPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mProgressPaint: Paint? = null

    // 圆环颜色 // 圆环背景颜色 // 当前进度 //水波颜色 // 水波背景色 //进度条和水波之间的距离 //进度百分比字体大小
    // //进度百分比字体颜色
    private var mRingColor = 0
    private var mRingBgColor = 0
    private var mWaterColor = 0
    private var mWaterBgColor = 0
    private var mFontSize = 0
    private var mTextColor = 0

    // 进度 //浪峰个数
    private var crestCount = 1.5f
    var mProgress = 10
    var mMaxProgress = 100

    // 画布中心点
    private var mCenterPoint: Point? = null

    // 圆环宽度
    private var mRingWidth = 0f
    private var mProgress2WaterWidth = 0f

    // 是否显示进度条 //是否显示进度百分比
    private var mShowProgress = false
    private var mShowNumerical = true

    /**
     * 产生波浪效果的因子
     */
    private var mWaveFactor = 0L

    /**
     * 正在执行波浪动画
     */
    private var isWaving = false

    /**
     * 振幅
     */
    private var mAmplitude = 30.0f // 20F

    /**
     * 波浪的速度
     */
    private var mWaveSpeed = 0.070f // 0.020F

    /**
     * 设置进度
     */
    private var mSetTarget = 0 // 0.020F

    /**
     * 水的透明度
     */
    private var mWaterAlpha = 255 // 255
    var attrInit: WaterWaveAttrInit? = null
    private var mHandler: MyHandler? = null

    private class MyHandler(host: WaterWaveProgress?) : Handler() {
        private var mWeakRef: WeakReference<WaterWaveProgress?>? = null
        private val refreshPeriod = 100
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (mWeakRef!!.get() != null) {
                mWeakRef!!.get()!!.invalidate()
                sendEmptyMessageDelayed(0, refreshPeriod.toLong())
            }
        }

        init {
            mWeakRef = WeakReference(host)
        }
    }

    constructor(paramContext: Context?) : super(paramContext) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        0
    ) {
    }

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        attrInit = WaterWaveAttrInit(context!!, attrs, defStyleAttr)
        init(context)
    }

    @SuppressLint("NewApi")
    private fun init(context: Context?) {
        mCenterPoint = Point()
        mRingColor = attrInit!!.getProgressColor()
        mRingBgColor = attrInit!!.getProgressBgColor()
        mWaterColor = attrInit!!.getWaterWaveColor()
        mWaterBgColor = attrInit!!.getWaterWaveBgColor()
        mRingWidth = attrInit!!.getProgressWidth().toFloat()
        mProgress2WaterWidth = attrInit!!.getProgress2WaterWidth().toFloat()
        mShowProgress = attrInit!!.isShowProgress()
        mShowNumerical = attrInit!!.isShowNumerical()
        mFontSize = attrInit!!.getFontSize()
        mTextColor = attrInit!!.getTextColor()
        mProgress = attrInit!!.getProgress()
        mMaxProgress = attrInit!!.getMaxProgress()

        // 如果手机版本在4.0以上,则开启硬件加速
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            // setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mRingPaint = Paint()
        mRingPaint!!.isAntiAlias = true
        mRingPaint!!.color = mRingColor // 圆环颜色
        mRingPaint!!.style = Paint.Style.STROKE
        mRingPaint!!.strokeWidth = mRingWidth // 圆环宽度
        mPaintWater = Paint()
        mPaintWater!!.strokeWidth = 1.0f
        mPaintWater!!.color = mWaterColor
        // mPaintWater.setColor(getResources().getColor(mWaterColor));
        mPaintWater!!.alpha = mWaterAlpha
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = mTextColor
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.textSize = mFontSize.toFloat()
        mProgressPaint = Paint()
        mProgressPaint!!.isAntiAlias = true
        mProgressPaint!!.color = Color.RED
        mProgressPaint!!.style = Paint.Style.FILL
        mProgressPaint!!.textSize = 88f
        mHandler = MyHandler(this)
    }

    fun animateWave() {
        if (!isWaving) {
            mWaveFactor = 0L
            isWaving = true
            mHandler!!.sendEmptyMessage(0)
        }
    }

    @SuppressLint("DrawAllocation", "NewApi")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 获取整个View（容器）的宽、高
        var width = width
        var height = height
        height = if (width < height) width else height
        width = height
        mAmplitude = width / 20f
        mCenterPoint!!.x = width / 2
        mCenterPoint!!.y = height / 2
        run {
            // 重新设置进度条的宽度和水波与进度条的距离,,至于为什么写在这,我脑袋抽了可以不
            mRingWidth = if (mRingWidth == 0f) (width / 20).toFloat() else mRingWidth
            mProgress2WaterWidth =
                if (mProgress2WaterWidth == 0f) mRingWidth * 0.6f else mProgress2WaterWidth
            mRingPaint!!.strokeWidth = mRingWidth
            mTextPaint!!.textSize =
                if (mFontSize == 0) (width / 5).toFloat() else mFontSize.toFloat()
            if (VERSION.SDK_INT == VERSION_CODES.JELLY_BEAN) {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
            } else {
                setLayerType(LAYER_TYPE_HARDWARE, null)
            }
        }
        val oval = RectF()
        oval.left = mRingWidth / 2
        oval.top = mRingWidth / 2
        oval.right = width - mRingWidth / 2
        oval.bottom = height - mRingWidth / 2
        if (isInEditMode) {
            mRingPaint!!.color = mRingBgColor
            canvas.drawArc(oval, -90f, 360f, false, mRingPaint!!)
            mRingPaint!!.color = mRingColor
            canvas.drawArc(oval, -90f, 90f, false, mRingPaint!!)
            canvas.drawCircle(
                mCenterPoint!!.x.toFloat(), mCenterPoint!!.y.toFloat(), mCenterPoint!!.x
                        - mRingWidth - mProgress2WaterWidth, mPaintWater!!
            )
            return
        }

        // 如果没有执行波浪动画，或者也没有指定容器宽高，就画个简单的矩形
        if (width == 0 || height == 0 || isInEditMode) {
            canvas.drawCircle(
                mCenterPoint!!.x.toFloat(),
                mCenterPoint!!.y.toFloat(),
                width / 2 - mProgress2WaterWidth - mRingWidth,
                mPaintWater!!
            )
            return
        }

        // 水与边框的距离
        val waterPadding: Float = if (mShowProgress) mRingWidth + mProgress2WaterWidth else 0F
        // 水最高处
        val waterHeightCount =
            if (mShowProgress) (height - waterPadding * 2).toInt() else height

        // 重新生成波浪的形状
        mWaveFactor++
        if (mWaveFactor >= Int.MAX_VALUE) {
            mWaveFactor = 0L
        }

        // 画进度条背景
        mRingPaint!!.color = mRingBgColor
        // canvas.drawArc(oval, -90, 360, false, mRingPaint);
        // //和下面效果一样,只不过这个是画个360度的弧,下面是画圆环
        canvas.drawCircle(
            width / 2.toFloat(), width / 2.toFloat(), waterHeightCount / 2
                    + waterPadding - mRingWidth / 2, mRingPaint!!
        )
        mRingPaint!!.color = mRingColor
        // 100为 总进度
        canvas.drawArc(
            oval, -90f, mProgress * 1f / mMaxProgress * 360f, false,
            mRingPaint!!
        )

        // 计算出水的高度
        val waterHeight = (waterHeightCount * (1 - mProgress * 1f / mMaxProgress)
                + waterPadding)
        val staticHeight = (waterHeight + mAmplitude).toInt()
        val mPath = Path()
        mPath.reset()
        if (mShowProgress) {
            mPath.addCircle(
                width / 2.toFloat(), width / 2.toFloat(), waterHeightCount / 2.toFloat(),
                Path.Direction.CCW
            )
        } else {
            mPath.addCircle(
                width / 2.toFloat(), width / 2.toFloat(), waterHeightCount / 2.toFloat(),
                Path.Direction.CCW
            )
        }
        // canvas添加限制,让接下来的绘制都在园内
        canvas.clipPath(mPath, Region.Op.INTERSECT)
        //		canvas.clipPath(mPath, Op.REPLACE);
        val bgPaint = Paint()
        bgPaint.color = mWaterBgColor
        // 绘制背景
        canvas.drawRect(
            waterPadding, waterPadding, waterHeightCount
                    + waterPadding, waterHeightCount + waterPadding, bgPaint
        )
        // 绘制静止的水
        canvas.drawRect(
            waterPadding, staticHeight.toFloat(), waterHeightCount
                    + waterPadding, waterHeightCount + waterPadding, mPaintWater!!
        )

        // 待绘制的波浪线的x坐标
        var xToBeDrawed = waterPadding.toInt()
        var waveHeight = (waterHeight - mAmplitude
                * Math.sin(
            Math.PI
                    * (2.0f * (xToBeDrawed + mWaveFactor * width
                    * mWaveSpeed)) / width
        )).toInt()
        // 波浪线新的高度
        var newWaveHeight = waveHeight
        while (true) {
            if (xToBeDrawed >= waterHeightCount + waterPadding) {
                break
            }
            // 根据当前x值计算波浪线新的高度
            newWaveHeight = (waterHeight - mAmplitude
                    * Math.sin(
                Math.PI
                        * (crestCount * (xToBeDrawed + mWaveFactor * waterHeightCount
                        * mWaveSpeed)) / waterHeightCount
            )).toInt()

            // 先画出梯形的顶边
            canvas.drawLine(
                xToBeDrawed.toFloat(), waveHeight.toFloat(), xToBeDrawed + 1.toFloat(),
                newWaveHeight.toFloat(), mPaintWater!!
            )

            // 画出动态变化的柱子部分
            canvas.drawLine(
                xToBeDrawed.toFloat(), newWaveHeight.toFloat(), xToBeDrawed + 1.toFloat(),
                staticHeight.toFloat(), mPaintWater!!
            )
            xToBeDrawed++
            waveHeight = newWaveHeight
        }
        if (mShowNumerical) {
            var targetM : String = mSetTarget.toString()
            if (mSetTarget > 999999) {
                targetM = "进度 : ${targetM[0]},${targetM[1]}${targetM[2]}${targetM[3]},${targetM[4]}${targetM[5]}${targetM[6]} ￥"
            }
            if (mSetTarget in 100000..999999) {
                targetM = "进度 : ${targetM[0]}${targetM[1]}${targetM[2]},${targetM[3]}${targetM[4]}${targetM[5]} ￥"
            }
            if (mSetTarget in 10000..99999) {
                targetM = "进度 : ${targetM[0]}${targetM[1]},${targetM[2]}${targetM[3]}${targetM[4]} ￥"
            }
            if (mSetTarget in 1000..9999) {
                targetM = "进度 : ${targetM[0]},${targetM[1]}${targetM[2]}${targetM[3]} ￥"
            }
            val targetTxt = "目标 : 1,000,000 ￥"
            val progressTxt = String.format(
                "%.0f", mSetTarget.toFloat() / 1000000
                        * 100f
            ) + "%"
            val mTxtWidth = mTextPaint!!.measureText(
                progressTxt, 0,
                progressTxt.length
            )
            canvas.drawText(
                targetTxt, mCenterPoint!!.x - 2 * mTxtWidth,
                mCenterPoint!!.x * 1.5f - 2.5f * mFontSize, mTextPaint!!
            )
            canvas.drawText(
                targetM, mCenterPoint!!.x - 2 * mTxtWidth,
                mCenterPoint!!.x * 1.5f - mFontSize, mTextPaint!!
            )
            canvas.drawText(
                progressTxt, mCenterPoint!!.x - mTxtWidth / 2,
                mCenterPoint!!.x * 1.5f + mFontSize, mProgressPaint!!
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = widthMeasureSpec
        var height = heightMeasureSpec
        height = if (width < height) width else height
        width = height
        setMeasuredDimension(width, height)
    }

    /**
     * 设置波浪的振幅
     */
    fun setAmplitude(amplitude: Float) {
        mAmplitude = amplitude
    }

    /**
     * 设置水的透明度
     *
     * @param alpha 透明的百分比，值为0到1之间的小数，越接近0越透明
     */
    fun setWaterAlpha(alpha: Float) {
        mWaterAlpha = (255.0f * alpha).toInt()
        mPaintWater!!.alpha = mWaterAlpha
    }

    /**
     * 设置水的颜色
     */
    fun setWaterColor(color: Int) {
        mWaterColor = color
    }

    /**
     * 获取进度 动画时会用到
     */
    open fun getProgress(): Int {
        return mProgress
    }
    /**
     * 设置当前进度
     */
    open fun setProgress(progress: Int): Unit {
        var progress = progress
        progress = if (progress > 100) 100 else if (progress < 0) 0 else progress
        mProgress = progress
        invalidate()
    }

    /**
     * 设置波浪速度
     */
    fun setWaveSpeed(speed: Float) {
        mWaveSpeed = speed
    }

    fun setTarget(target: Int) {
        mSetTarget = target
    }

    /**
     * 是否显示进度条
     *
     * @param b
     */
    fun setShowProgress(b: Boolean) {
        mShowProgress = b
    }

    /**
     * 是否显示进度值
     *
     * @param b
     */
    fun setShowNumerical(b: Boolean) {
        mShowNumerical = b
    }

    /**
     * 设置进度条前景色
     *
     * @param mRingColor
     */
    fun setmRingColor(mRingColor: Int): Unit {
        this.mRingColor = mRingColor
    }

    /**
     * 设置进度条背景色
     *
     * @param mRingBgColor
     */
    fun setmRingBgColor(mRingBgColor: Int) {
        this.mRingBgColor = mRingBgColor
    }

    /**
     * 设置水波颜色
     *
     * @param mWaterColor
     */
    fun setmWaterColor(mWaterColor: Int) {
        this.mWaterColor = mWaterColor
    }

    /**
     * 设置水波背景色
     *
     * @param mWaterBgColor
     */
    fun setWaterBgColor(mWaterBgColor: Int) {
        this.mWaterBgColor = mWaterBgColor
    }

    /**
     * 设置进度值显示字体大小
     *
     * @param mFontSize
     */
    fun setFontSize(mFontSize: Int) {
        this.mFontSize = mFontSize
    }

    /**
     * 设置进度值显示字体颜色
     *
     * @param mTextColor
     */
    fun setTextColor(mTextColor: Int) {
        this.mTextColor = mTextColor
    }

    /**
     * 设置进度条最大值
     *
     * @param mMaxProgress
     */
    fun setMaxProgress(mMaxProgress: Int) {
        this.mMaxProgress = mMaxProgress
    }

    /**
     * 设置浪峰个数
     *
     * @param crestCount
     */
    fun setCrestCount(crestCount: Float) {
        this.crestCount = crestCount
    }

    /**
     * 设置进度条宽度
     *
     * @param mRingWidth
     */
    fun setRingWidth(mRingWidth: Float) {
        this.mRingWidth = mRingWidth
    }

    /**
     * 设置水波到进度条之间的距离
     *
     * @param mProgress2WaterWidth
     */
    fun setProgress2WaterWidth(mProgress2WaterWidth: Float) {
        this.mProgress2WaterWidth = mProgress2WaterWidth
    }
}