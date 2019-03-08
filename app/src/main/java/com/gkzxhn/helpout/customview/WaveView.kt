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
            wavePath2 = initWavePath(initPoints(waveWidth2, waveRatio2), waveWidth2, waveHeight2)

            startWave()
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
    private var tempTime = 0L

    private var startTime = 0L
    private var startTime2 = 0L
    private var offsetCom = 0F
    private var offsetCom2 = 0F
    private fun startWave() {
        anim = ValueAnimator.ofFloat(0f, 1f)
        anim?.duration = 1 * 1000
        anim?.repeatCount = ValueAnimator.INFINITE
        anim?.repeatMode = ValueAnimator.RESTART
        anim?.interpolator = LinearInterpolator()
        anim?.addUpdateListener(updateListener)
        startTime = System.currentTimeMillis()
        startTime2 = startTime
        tempTime = startTime
        anim?.start()
    }

    private val updateListener = object : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(it: ValueAnimator?) {
            val temp = System.currentTimeMillis()
//            LogUtil.d("temp time: ", (temp - tempTime).toString())
            if ((temp - tempTime) > 30) {
                //30毫秒/帧以上再刷新
                //位移差
                val offset = waveSpeed.toFloat().dp2px() * (temp - tempTime) / 1000
                val offset2 = waveSpeed2.toFloat().dp2px() * (temp - tempTime) / 1000

                //总时间
                val time = temp - startTime
                val time2 = temp - startTime2
                if (time > waveWidth / waveSpeed.toFloat().dp2px() * 1000) {
                    startTime = temp
                    wavePath?.offset(offsetCom, 0f)
                    offsetCom = 0F
                } else {
                    offsetCom += offset
                    wavePath?.offset(-offset, 0f)
                }
                if (time2 > waveWidth2 / waveSpeed2.toFloat().dp2px() * 1000) {
                    startTime2 = temp
                    wavePath2?.offset(offsetCom2, 0f)
                    offsetCom2 = 0F
                } else {
                    offsetCom2 += offset2
                    wavePath2?.offset(-offset2, 0f)
                }
                invalidate()
                tempTime = temp
            }
        }
    }


    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            anim?.cancel()
        } else if(visibility == View.VISIBLE) {
            if (anim?.isStarted == false) {
                anim?.start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        anim?.cancel()
        anim?.removeAllUpdateListeners()
    }
}
