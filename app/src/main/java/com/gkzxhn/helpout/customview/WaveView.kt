package com.gkzxhn.helpout.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.extensions.dp2px
import kotlin.math.roundToInt


class WaveView : View {

    val DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3f74ce")
    val DEFAULT_FRONT_WAVE_WIDTH = 200f
    val DEFAULT_FRONT_WAVE_HEIGHT = 10f
    val DEFAULT_FRONT_WAVE_SPEED = 50
    val DEFAULT_FRONT_WAVE_RATIO = 0.2f

    val DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#7a8598")
    val DEFAULT_BEHIND_WAVE_HEIGHT = 12f
    val DEFAULT_BEHIND_WAVE_WIDTH = 300f
    val DEFAULT_BEHIND_WAVE_SPEED = 50
    val DEFAULT_BEHIND_WAVE_RATIO2 = 0.5f

    private var mViewPaint: Paint? = null
    private var wavePath: Path? = null
    private var pointList = ArrayList<Point>()

    private var mViewPaint2: Paint? = null
    private var wavePath2: Path? = null
    private var pointList2 = ArrayList<Point>()

    private var waveWidth: Float = DEFAULT_FRONT_WAVE_WIDTH
    private var waveHeight: Float = DEFAULT_FRONT_WAVE_HEIGHT
    private var waveColor: Int = DEFAULT_FRONT_WAVE_COLOR
    private var waveSpeed: Int = DEFAULT_FRONT_WAVE_SPEED
    private var waveRatio: Float = DEFAULT_FRONT_WAVE_RATIO

    private var waveWidth2: Float = DEFAULT_BEHIND_WAVE_WIDTH
    private var waveHeight2: Float = DEFAULT_BEHIND_WAVE_HEIGHT
    private var waveColor2: Int = DEFAULT_BEHIND_WAVE_COLOR
    private var waveSpeed2: Int = DEFAULT_BEHIND_WAVE_SPEED
    private var waveRatio2: Float = DEFAULT_BEHIND_WAVE_RATIO2

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        waveWidth = ta.getDimension(R.styleable.WaveView_wave_width, DEFAULT_FRONT_WAVE_WIDTH)
        waveHeight = ta.getDimension(R.styleable.WaveView_wave_height, DEFAULT_FRONT_WAVE_HEIGHT)
        waveColor = ta.getColor(R.styleable.WaveView_wave_color, DEFAULT_FRONT_WAVE_COLOR)
        waveSpeed = ta.getInteger(R.styleable.WaveView_speed, DEFAULT_FRONT_WAVE_SPEED)
        waveRatio = ta.getFloat(R.styleable.WaveView_wave_ratio, DEFAULT_FRONT_WAVE_RATIO)

        waveWidth2 = ta.getDimension(R.styleable.WaveView_wave_width2, DEFAULT_BEHIND_WAVE_WIDTH)
        waveHeight2 = ta.getDimension(R.styleable.WaveView_wave_height2, DEFAULT_BEHIND_WAVE_HEIGHT)
        waveColor2 = ta.getColor(R.styleable.WaveView_wave_color2, DEFAULT_BEHIND_WAVE_COLOR)
        waveSpeed2 = ta.getInteger(R.styleable.WaveView_speed2, DEFAULT_BEHIND_WAVE_SPEED)
        waveRatio2 = ta.getFloat(R.styleable.WaveView_wave_ratio2, DEFAULT_BEHIND_WAVE_RATIO2)
        ta.recycle()

        initPaint()
    }

    private fun initPaint() {
        mViewPaint = Paint()
        mViewPaint?.isAntiAlias = true
        mViewPaint?.color = waveColor
        mViewPaint?.style = Paint.Style.FILL

        mViewPaint2 = Paint()
        mViewPaint2?.isAntiAlias = true
        mViewPaint2?.color = waveColor2
        mViewPaint2?.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        if (!hasInitWave) {

            wavePath = initWavePath(initPoints(waveWidth, waveRatio), waveWidth, waveHeight)
            wavePath?.let { startWave(it, waveSpeed) }

            wavePath2 = initWavePath(initPoints(waveWidth2, waveRatio2), waveWidth2, waveHeight2)
            wavePath2?.let { startWave2(it, waveSpeed2) }
            hasInitWave = true
        }

    }

    private var hasInitWave = false

    private fun initPoints(waveWidth: Float, ratio: Float): ArrayList<Point> {
        val pointList = arrayListOf<Point>()
//        List.clear()
        val n = Math.round(measuredWidth / waveWidth)
        var startX = 0
        for (i in 0..4 * n + 1) {
            val point = Point(startX, (measuredHeight * (1 - ratio)).roundToInt())
            startX += waveWidth.roundToInt()
            pointList.add(point)
        }
        return pointList
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(wavePath2!!, mViewPaint2!!)
        canvas?.drawPath(wavePath!!, mViewPaint!!)
    }

    private fun initWavePath(points: List<Point>, waveWidth: Float, waveHeight: Float): Path? {
        val wavePath = Path()
        points.forEach {
            wavePath?.moveTo(it.x.toFloat(), it.y.toFloat())
            val controlPoint1 = Point(it.x + (waveWidth / 4).toInt(), it.y - waveHeight.roundToInt())
            val controlPoint2 = Point(it.x + (waveWidth / 4 * 3).toInt(), it.y + waveHeight.roundToInt())
            val endPoint1 = Point((it.x + waveWidth / 2).roundToInt(), it.y)
            val endPoint2 = Point((it.x + waveWidth).roundToInt(), it.y)
            wavePath?.quadTo(controlPoint1.x.toFloat(), controlPoint1.y.toFloat(), endPoint1.x.toFloat(), endPoint1.y.toFloat())
            wavePath?.quadTo(controlPoint2.x.toFloat(), controlPoint2.y.toFloat(), endPoint2.x.toFloat(), endPoint2.y.toFloat())
        }
        wavePath?.rLineTo(0f, measuredHeight.toFloat())
        wavePath?.lineTo(0f, measuredHeight.toFloat())
        wavePath?.lineTo(points[0].x.toFloat(), points[0].y.toFloat())
        return wavePath
    }

    private var anim: ValueAnimator? = null
    private var tempTranslateX = 0f

    private var anim2: ValueAnimator? = null
    private var tempTranslateX2 = 0f
    private fun startWave(wavePath: Path, speed: Int) {
        anim = ValueAnimator.ofFloat(0f, -waveWidth)
        anim?.duration = (waveWidth / speed.toFloat().dp2px() * 1000).toLong()
        anim?.repeatCount = ValueAnimator.INFINITE
        anim?.repeatMode = ValueAnimator.RESTART
        anim?.interpolator = LinearInterpolator()
        anim?.addUpdateListener {
            wavePath.offset(it.animatedValue as Float - tempTranslateX, 0f)
            tempTranslateX = it.animatedValue as Float
            invalidate()
        }
        anim?.start()
    }

    private fun startWave2(wavePath: Path, speed: Int) {
        anim2 = ValueAnimator.ofFloat(0f, -waveWidth2)
        anim2?.duration = (waveWidth2 / speed.toFloat().dp2px() * 1000).toLong()
        anim2?.repeatCount = ValueAnimator.INFINITE
        anim2?.repeatMode = ValueAnimator.RESTART
        anim2?.interpolator = LinearInterpolator()
        anim2?.addUpdateListener {
            wavePath.offset(it.animatedValue as Float - tempTranslateX2, 0f)
            tempTranslateX2 = it.animatedValue as Float
            invalidate()
        }
        anim2?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        anim?.removeAllUpdateListeners()
        anim2?.removeAllUpdateListeners()
    }
}
