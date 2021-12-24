//package com.example.xingliansdk.view
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.*
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.View
//import com.amap.api.mapcore.util.it
//import com.example.xingliansdk.R
//import jxl.write.DateTime
//import java.lang.Math.abs
//
//class SleepDayChart (context: Context, attrs: AttributeSet?) : View(context, attrs) {
////屏幕宽高
//
//    private var scrWidth = 0f
//
//    private var scrHeight = 0f
//
//    private var xData: Array = arrayOf("20:00", "02:00", "08:00", "14:00", "20:00")
//
//    private var sleepsData: Sleep? = null
//
//    private lateinit var paintLine: Paint
//
//    private lateinit var paintGradientLine: Paint
//
//    private lateinit var paintXText: Paint
//
//    private lateinit var paintSleep: Paint
//
//    private lateinit var paintPillar: Paint
//
//    private lateinit var paintRound: Paint
//
//    private lateinit var paintBessel: Paint
//
//    private var xSlider = 0f //滑块的x轴位置
//
//    private var mPath: Path
//
//    private val curveCircleRadius = 12f
//
//// the coordinates of the first curve
//
//    private val mFirstCurveStartPoint = Point()
//
//    private val mFirstCurveEndPoint = Point()
//
//    private val mFirstCurveControlPoint1 = Point()
//
//    private val mFirstCurveControlPoint2 = Point()
//
////the coordinates of the second curve
//
//    private var mSecondCurveStartPoint = Point()
//
//    private val mSecondCurveEndPoint = Point()
//
//    private val mSecondCurveControlPoint1 = Point()
//
//    private val mSecondCurveControlPoint2 = Point()
//
//    init {
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
//
//        mPath = Path()
//
//        initPaint()
//
//    }
//
//    /**
//
//     * 初始化画笔
//
//     */
//
//    private fun initPaint() {
//        paintLine = Paint()
//
//        paintLine.style = Paint.Style.STROKE
//
//        paintLine.strokeWidth = 1f
//
//        paintLine.color = context.getColor(R.color.color_deep_sleep)
//
//        paintGradientLine = Paint()
//
//        paintGradientLine.style = Paint.Style.STROKE
//
//        paintGradientLine.strokeWidth = 1f
//
//        paintXText = Paint()
//
//        paintXText.isAntiAlias = true
//
//        paintXText.strokeWidth = 1f
//
//        paintXText.textSize = 12f
//
//        paintXText.textAlign = Paint.Align.CENTER
//
//        paintXText.color = context.getColor(R.color.color_light_sleep)
//
//        paintSleep = Paint()
//
//        paintSleep.style = Paint.Style.FILL
//
//        paintSleep.isAntiAlias = true
//
//        paintSleep.color = context.getColor(R.color.blue_7fbeff)
//
//        paintPillar = Paint()
//
//        paintPillar.style = Paint.Style.FILL
//
//        paintPillar.isAntiAlias = true
//
//        paintPillar.color = context.getColor(R.color.blue_7fbeff)
//
//        paintRound = Paint()
//
//        paintRound.style = Paint.Style.FILL
//
//        paintRound.isAntiAlias = true
//
//        paintRound.color = context.getColor(R.color.ffffff_6e6e6e)
//
//        paintBessel = Paint()
//
//        paintBessel.style = Paint.Style.FILL
//
//        paintBessel.isAntiAlias = true
//
//        paintBessel.color = context.getColor(R.color.f2f2f2_1d1d1d)
//
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//
//        scrWidth = width.toFloat()
//
//        scrHeight = height.toFloat()
//
//        ySpacing = scrHeight / 8f //y轴分8份
//
////底部圆滑块可以滑动的范围
//
//        xWithStart = margin * 3
//
//        xWithEnd = scrWidth - margin * 3
//
//        xSlider = scrWidth / 2
//
//        xSpacing = (xWithEnd - xWithStart) / (xData.size - 1)
//
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        parent.requestDisallowInterceptTouchEvent(true)
//
//        return super.dispatchTouchEvent(ev)
//
//    }
//
//    private var mDownX = 0f
//
//    private var mDownY = 0f
//
//    private var isSlider = false
//
//    @SuppressLint("ClickableViewAccessibility")
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                mDownX = event.x
//
//                mDownY = event.y
//
//                isSlider = abs(event.x - xSlider) < 60f && abs(event.y - ySpacing * 7) < 60f
//
//                return isSlider
//
//            }
//
//            MotionEvent.ACTION_MOVE ->
//
//                if (abs(event.y - mDownY) < abs(event.x - mDownX)) {
//                    if (isSlider) {
//                        xSlider = event.x
//
//                        if (xSlider < xWithStart) {
//                            xSlider = xWithStart
//
//                        }
//
//                        if (xSlider > xWithEnd) {
//                            xSlider = xWithEnd
//
//                        }
//
//                        invalidate()
//
//                    }
//
//                }
//
//            MotionEvent.ACTION_UP -> {
//                if (!isSlider) {
//                    if (abs(event.x - mDownX) < curveCircleRadius) {
//                        xSlider = event.x
//
//                        invalidate()
//
//                    }
//
//                }
//
//            }
//
//        }
//
//        return true
//
//    }
//
//    private val margin = 20f //左右两边距离
//
//    private var xWithStart = 0f //x轴的起始点
//
//    private var xWithEnd = 0f //x轴结束点
//
//    private var ySpacing = 0f //高度分割份数后间距
//
//    private var xSpacing = 0f //x轴分割份数后间距
//
//    @SuppressLint("DrawAllocation")
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
////画柱子
//
//        drawPillar(canvas)
//
////垂直渐变线
//
//        drawGradientLine(canvas)
//
////底部
//
//        drawBessel(canvas)
//
////画x轴方向文字
//
//        drawX(canvas)
//
//    }
//
//    private fun drawX(canvas: Canvas) {
//        if (sleepsData == null) {
//            xData.forEachIndexed { index, s ->
//
//                val x = xWithStart + xSpacing * index
//
//                val dis = abs(x - xSlider)
//
//                var y = ySpacing * 7 - 10f
//
//                if (dis < xSpacing / 2) {
//                    paintXText.typeface = Typeface.DEFAULT_BOLD
//
//                    y -= 10f * (1 - dis / xSpacing)
//
//                } else {
//                    paintXText.typeface = Typeface.DEFAULT
//
//                }
//
//                canvas.drawText(s, x, y, paintXText)
//
//                if (index == 0) {
//                    canvas.drawText(startDay, x, y - 12f, paintXText)
//
//                }
//
//                if (index == xData.size - 1) {
//                    canvas.drawText(endDay, x, y - 12f, paintXText)
//
//                }
//
//            }
//
//        } else {
//            sleepsData?.let {
//                val start = DateTime(it.items[0].timeStamp * 1000)
//
//                val asleep = start.hourOfDay * 60 + start.minuteOfHour
//
//                val end = DateTime(it.items.last().timeStamp * 1000)
//
//                val wakeUp = end.hourOfDay * 60 + end.minuteOfHour + it.items.last().duration
//
//                val s1 =
//
//                    "${"time"} ${asleep / 60}:${if (asleep % 60 < 10) "0" else ""}${asleep % 60}"
//
//                val dis1 = abs(xWithStart + paintXText.measureText(s1) / 2 - xSlider)
//
//                var y1 = ySpacing * 7 - 10f
//
//                if (dis1 < curveCircleRadius * 3) {
//                    paintXText.typeface = Typeface.DEFAULT_BOLD
//
//                    var temp = 1 - dis1 / curveCircleRadius * 2
//
//                    if (temp < 0f || temp > 1f) {
//                        temp = 1f
//
//                    }
//
//                    y1 -= 60f * temp
//
//                } else {
//                    paintXText.typeface = Typeface.DEFAULT
//
//                }
//
//                canvas.drawText(s1, xWithStart, y1, paintXText)
//
//                canvas.drawText(startDay, xWithStart, y1 - 40f, paintXText)
//
//                val hour = "${if (wakeUp / 60 < 10) "0" else ""}${wakeUp / 60}"
//
//                val minute = "${if (wakeUp % 60 < 10) "0" else ""}${wakeUp % 60}"
//
//                val s2 =
//
//                    "${"rise_time"} $hour:$minute"
//
//                val dis2 = abs(xWithEnd - paintXText.measureText(s2) / 2 - xSlider)
//
//                var y2 = ySpacing * 7 - 10f
//
//                if (dis2 < curveCircleRadius * 3) {
//                    paintXText.typeface = Typeface.DEFAULT_BOLD
//
//                    y2 -= 60f * (1 - dis2 / (xSlider - curveCircleRadius * 3))
//
//                } else {
//                    paintXText.typeface = Typeface.DEFAULT
//
//                }
//
//                canvas.drawText(s2, xWithEnd, y2, paintXText)
//
//                canvas.drawText(endDay, xWithEnd, y2 - 40f, paintXText)
//
//            }
//
//        }
//
//    }
//
//    private fun drawPillar(canvas: Canvas) {
//        var top = 0f
//
//        var bottom = 0f
//
//        var preDuration = 0 //前一状态时长
//
//        var duration = 0 //时间累加
//
//        var tempTop = 0f
//
//        var tempBottom: Float
//
//        var startColor = 0
//
//        var endColor = 0
//
//        val colors = intArrayOf(startColor, endColor)
//
//        sleepsData?.let {
//            it.items.forEachIndexed { index, item ->
//
//                when (item.status) {
//                    3, 4 -> { //清醒
//
//                        endColor = Color.parseColor("#fdc221")
//
//                        paintSleep.color = Color.parseColor("#fdc221")
//
//                        paintPillar.color = Color.parseColor("#f9eec1")
//
//                        top = 1f
//
//                        bottom = 2f
//
//                    }
//
//                    12 -> { //快速眼动
//
//                        endColor = Color.parseColor("#fd817c")
//
//                        paintSleep.color = Color.parseColor("#fd817c")
//
//                        paintPillar.color = Color.parseColor("#4dfd817c")
//
//                        top = 2f
//
//                        bottom = 3f
//
//                    }
//
//                    0, 1 -> { //浅
//
//                        endColor = Color.parseColor("#c64be4")
//
//                        paintSleep.color = Color.parseColor("#c64be4")
//
//                        paintPillar.color = Color.parseColor("#e8c3f1")
//
//                        top = 3f
//
//                        bottom = 4f
//
//                    }
//
//                    2 -> { //深
//
//                        endColor = Color.parseColor("#8a2be2")
//
//                        paintSleep.color = Color.parseColor("#8a2be2")
//
//                        paintPillar.color = Color.parseColor("#d6b9f1")
//
//                        top = 4f
//
//                        bottom = 5f
//
//                    }
//
//                }
//
//                if (xSlider < xWithStart + xSpacing * (duration + item.duration) && xSlider > xWithStart + xSpacing * duration) {
//                    onDaySelectListener?.invoke(index, item)
//
//                    canvas.drawRect(
//
//                        RectF(
//
//                            xWithStart + xSpacing * duration,
//
//                            ySpacing * top + 10f,
//
//                            xWithStart + xSpacing * (duration + item.duration),
//
//                            ySpacing * 7
//
//                        ), paintPillar
//
//                    )
//
//                }
//
//                canvas.drawRoundRect(
//
//                    RectF(
//
//                        xWithStart + xSpacing * duration - 1f,
//
//                        ySpacing * top,
//
//                        xWithStart + xSpacing * (duration + item.duration) + 1f,
//
//                        ySpacing * bottom
//
//                    ), 10f, 10f, paintSleep
//
//                )
//
//                if (index > 0 && index < it.items.size) {
//                    if (tempTop < top) {
//                        tempTop += 0.9f
//
//                        tempBottom = bottom - 0.9f
//
//                        colors[0] = startColor
//
//                        colors[1] = endColor
//
//                        if (xSpacing * preDuration > 10f) {
//                            val path1 = Path()
//
//                            path1.moveTo(xWithStart + xSpacing * duration, ySpacing * tempTop)
//
//                            path1.lineTo(
//
//                                xWithStart + xSpacing * duration - 8f,
//
//                                ySpacing * tempTop + 6f
//
//                            )
//
//                            path1.lineTo(xWithStart + xSpacing * duration, ySpacing * tempTop + 12f)
//
//                            path1.close()
//
//                            paintSleep.color = startColor
//
//                            canvas.drawPath(path1, paintSleep)
//
//                        }
//
//                        if (xSpacing * item.duration > 10f) {
//                            val path2 = Path()
//
//                            path2.moveTo(xWithStart + xSpacing * duration, ySpacing * tempBottom)
//
//                            path2.lineTo(
//
//                                xWithStart + xSpacing * duration + 8f,
//
//                                ySpacing * tempBottom - 6f
//
//                            )
//
//                            path2.lineTo(
//
//                                xWithStart + xSpacing * duration,
//
//                                ySpacing * tempBottom - 12f
//
//                            )
//
//                            path2.close()
//
//                            paintSleep.color = endColor
//
//                            canvas.drawPath(path2, paintSleep)
//
//                        }
//
//                    } else {
//                        tempBottom = tempTop + 0.1f
//
//                        tempTop = bottom - 0.1f
//
//                        colors[0] = endColor
//
//                        colors[1] = startColor
//
//                        if (xSpacing * preDuration > 10f) {
//                            val path1 = Path()
//
//                            path1.moveTo(xWithStart + xSpacing * duration, ySpacing * tempBottom)
//
//                            path1.lineTo(
//
//                                xWithStart + xSpacing * duration - 8f,
//
//                                ySpacing * tempBottom - 6f
//
//                            )
//
//                            path1.lineTo(
//
//                                xWithStart + xSpacing * duration,
//
//                                ySpacing * tempBottom - 12f
//
//                            )
//
//                            path1.close()
//
//                            paintSleep.color = startColor
//
//                            canvas.drawPath(path1, paintSleep)
//
//                        }
//
//                        if (xSpacing * item.duration > 10f) {
//                            val path2 = Path()
//
//                            path2.moveTo(xWithStart + xSpacing * duration, ySpacing * tempTop)
//
//                            path2.lineTo(
//
//                                xWithStart + xSpacing * duration + 8f,
//
//                                ySpacing * tempTop + 6f
//
//                            )
//
//                            path2.lineTo(xWithStart + xSpacing * duration, ySpacing * tempTop + 12f)
//
//                            path2.close()
//
//                            paintSleep.color = endColor
//
//                            canvas.drawPath(path2, paintSleep)
//
//                        }
//
//                    }
//
//                    val mLinearGradient = LinearGradient(
//
//                        xWithStart + xSpacing * duration,
//
//                        ySpacing * tempTop,
//
//                        xWithStart + xSpacing * duration,
//
//                        ySpacing * tempBottom, colors, null, Shader.TileMode.MIRROR
//
//                    )
//
//                    paintGradientLine.shader = mLinearGradient
//
//                    canvas.drawLine(
//
//                        xWithStart + xSpacing * duration,
//
//                        ySpacing * tempTop,
//
//                        xWithStart + xSpacing * duration,
//
//                        ySpacing * tempBottom,
//
//                        paintGradientLine
//
//                    )
//
//                }
//
//                tempTop = top
//
//                tempBottom = bottom
//
//                preDuration = item.duration
//
//                duration += item.duration
//
//                startColor = endColor
//
//            }
//
//        }
//
//    }
//
//    private fun drawBessel(canvas: Canvas) {
//// 第一条曲线开始点
//
//        mFirstCurveStartPoint[(xSlider - curveCircleRadius * 3).toInt()] = (ySpacing * 7).toInt()
//
//// 第一条曲线结束点
//
//        mFirstCurveEndPoint[xSlider.toInt()] =
//
//            (ySpacing * 7 - curveCircleRadius - curveCircleRadius / 4).toInt()
//
//// 第二条开始点
//
//        mSecondCurveStartPoint = mFirstCurveEndPoint
//
//        mSecondCurveEndPoint[(xSlider + curveCircleRadius * 3).toInt()] = (ySpacing * 7).toInt()
//
//// 第一条控制点
//
//        mFirstCurveControlPoint1[(mFirstCurveStartPoint.x + curveCircleRadius + curveCircleRadius / 4).toInt()] =
//
//            mFirstCurveStartPoint.y
//
//        mFirstCurveControlPoint2[(mFirstCurveEndPoint.x - curveCircleRadius * 2 + curveCircleRadius).toInt()] =
//
//            mFirstCurveEndPoint.y
//
//// 第二条控制点
//
//        mSecondCurveControlPoint1[(mSecondCurveStartPoint.x + curveCircleRadius * 2 - curveCircleRadius).toInt()] =
//
//            mSecondCurveStartPoint.y
//
//        mSecondCurveControlPoint2[(mSecondCurveEndPoint.x - curveCircleRadius - curveCircleRadius / 4).toInt()] =
//
//            mSecondCurveEndPoint.y
//
//        mPath.reset()
//
//        mPath.moveTo(0f, ySpacing * 7)
//
//        mPath.lineTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat())
//
//        mPath.cubicTo(
//
//            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
//
//            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
//
//            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
//
//        )
//
//        mPath.cubicTo(
//
//            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
//
//            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
//
//            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
//
//        )
//
//        mPath.lineTo(scrWidth, ySpacing * 7)
//
//        mPath.lineTo(scrWidth, scrHeight)
//
//        mPath.lineTo(0f, scrHeight)
//
//        mPath.close()
//
////底部灰色
//
//        canvas.drawPath(mPath, paintBessel)
//
////底部滑块
//
//        canvas.drawCircle(xSlider, ySpacing * 7 + 5f, curveCircleRadius, paintRound)
//
//    }
//
//    private var startDay = ""
//
//    private var endDay = ""
//
//    fun setValue(value: Sleep?, startDay: String, endDay: String): SleepDayChart {
//        this.startDay = startDay
//
//        this.endDay = endDay
//
//        this.sleepsData = value
//
//        if (sleepsData == null) {
//            xSpacing = (xWithEnd - xWithStart) / (xData.size - 1)
//
//        } else {
//            sleepsData?.let {
//                xSpacing = (xWithEnd - xWithStart) / it.total //时间段分割成分钟
//
//            }
//
//        }
//
//        postInvalidate()
//
//        return this
//
//    }
//
//    private fun drawGradientLine(canvas: Canvas) {
//        if (sleepsData == null) {
//            canvas.drawText(
//
//                context.getString(R.string.no_sleep_data),
//
//                scrWidth / 2f,
//
//                scrHeight / 2f,
//
//                paintXText
//
//            )
//
//        } else {
//            val mLinearGradient = LinearGradient(
//
//                xSlider, ySpacing, xSlider, ySpacing * 6,
//
//                intArrayOf(
//
//                    context.colorCompat(R.color.ffffff_262626), Color.parseColor("#0e83ff"),
//
//                    context.colorCompat(R.color.ffffff_262626)
//
//                ), null, Shader.TileMode.MIRROR
//
//            )
//
//            paintGradientLine.shader = mLinearGradient
//
//            if (ySpacing > 0) {
//                canvas.drawLine(xSlider, ySpacing, xSlider, ySpacing * 6, paintGradientLine)
//
//            }
//
//        }
//
//    }
//
//    private var onDaySelectListener: ((index: Int, item: SleepItem) -> Unit)? = null
//
//    fun setOnDaySelectListener(l: ((index: Int, item: SleepItem) -> Unit)): SleepDayChart {
//        this.onDaySelectListener = l
//
//        return this
//
//    }
//
//}