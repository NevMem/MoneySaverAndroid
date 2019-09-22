package com.nevmem.moneysaver.app.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.nevmem.moneysaver.R

class BarChart(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private var showAmount = 20
    private var rounded = false
    private var barWidth = 20
    private var baseColor: Int = Color.parseColor("#FFFFFF")
    private var dividersFontColor: Int = Color.parseColor("#AAAAAA")
    private var liningColor: Int = Color.parseColor("#15ffffff")
    private var leftBarSize = 60
    private var divCount = 5
    private var showLabels = true
    private val paint = Paint()

    var values = ArrayList<Double>()
    var multiplier = 1f

    init {
        val gt = context.theme.obtainStyledAttributes(attrs, R.styleable.BarChart, 0, 0)
        showAmount = gt.getInteger(R.styleable.BarChart_amountOfBars, showAmount)
        rounded = gt.getBoolean(R.styleable.BarChart_rounded, rounded)
        barWidth = gt.getInteger(R.styleable.BarChart_barWidth, barWidth)
        baseColor = gt.getColor(R.styleable.BarChart_baseColor, baseColor)
        dividersFontColor = gt.getColor(R.styleable.BarChart_labelsFontColor, dividersFontColor)
        showLabels = gt.getBoolean(R.styleable.BarChart_showLabels, showLabels)
        liningColor = gt.getColor(R.styleable.BarChart_labelsLiningColor, liningColor)
        gt.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { // TODO: (use dimension modes)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    private fun drawBar(canvas: Canvas?, dx: Int, dy: Int, barWidth: Int, barHeight: Int) {
        val paint = Paint()
        paint.color = baseColor
        if (!rounded) {
            canvas!!.drawRect(
                (dx - barWidth / 2).toFloat(),
                (dy - barHeight).toFloat(),
                (dx + barWidth / 2).toFloat(),
                dy.toFloat(),
                paint
            )
        } else {
            canvas!!.drawRoundRect(
                (dx - barWidth / 2).toFloat(),
                (dy - barHeight).toFloat(),
                (dx + barWidth / 2).toFloat(),
                dy.toFloat(),
                barWidth / 2.toFloat(), barWidth / 2.toFloat(),
                paint
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = Color.parseColor("#191919")
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (values.size != 0) {
            var index = values.size - showAmount
            if (index < 0)
                index = 0
            var mx = values[index]
            for (i in index until (values.size))
                if (mx < values[i])
                    mx = values[i]

            var smx = mx * 1.1f
            smx = (((smx + 999) / 1000).toInt()) * 1000.0

            if (showLabels) {
                for (i in 0 until (divCount + 1)) {
                    val cur = smx * i / divCount
                    paint.textSize = 18f
                    paint.color = dividersFontColor
                    canvas.drawText(
                        cur.toInt().toString(), paddingLeft.toFloat(),
                        height - paddingBottom - (height - paddingTop - paddingBottom) / divCount.toFloat() * i, paint
                    )
                    paint.color = liningColor
                    canvas.drawLine(
                        paddingLeft.toFloat(),
                        height - paddingBottom - (height - paddingTop - paddingBottom) / divCount.toFloat() * i,
                        width.toFloat(),
                        height - paddingBottom - (height - paddingTop - paddingBottom) / divCount.toFloat() * i,
                        paint
                    )
                }
            }
            for (i in index until (values.size)) {
                var dx = paddingLeft +
                        (width - paddingLeft - paddingRight) * (i - index) / showAmount +
                        width / showAmount / 2
                if (showLabels) dx = leftBarSize + paddingLeft +
                        (width - leftBarSize - paddingLeft - paddingRight) * (i - index) / showAmount +
                        width / showAmount / 2
                drawBar(
                    canvas,
                    dx,
                    height - paddingBottom,
                    barWidth,
                    (values[i] * multiplier * (height - paddingTop - paddingBottom) / smx).toInt()
                )
            }
        } else {
            paint.color = Color.parseColor("#9AC7C7C7")
            paint.textSize = 40f
            val str = "Data is empty"
            val textWidth = paint.measureText(str)
            canvas.drawText(str, width / 2 - textWidth / 2, height.toFloat() / 2 + 40f / 2, paint)
        }
    }
}