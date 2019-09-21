package com.nevmem.moneysaver.app.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.common.exceptions.WrongChartDataException
import com.nevmem.moneysaver.app.utils.UnitsHelper

class PieChart(private var ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private var paint: Paint = Paint(0)
    private var values: ArrayList<Double> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()

    companion object {
        var baseColors: ArrayList<Int> = arrayListOf(
            Color.parseColor("#03f7eb"),
            Color.parseColor("#18ff6d"),
            Color.parseColor("#ff9505"),
            Color.parseColor("#5b2a86"),
            Color.parseColor("#f2545b"),
            Color.parseColor("#fffd82"),
            Color.parseColor("#a9e5bb"),
            Color.parseColor("#eee82c"),
            Color.parseColor("#32908f"),
            Color.parseColor("#26c485"),
            Color.parseColor("#ff6542"),
            Color.parseColor("#7d8cc4")
        )

        const val ringWidth = 24f
    }

    private var chartBackgroundColor = Color.parseColor("#191919")

    private var chartPadding = 10.0f // Default value

    private var valuesSum: Double = 0.0

    init {
        paint.flags = paint.flags.or(Paint.ANTI_ALIAS_FLAG)

        val gt = context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0)
        chartPadding = gt.getFloat(R.styleable.PieChart_chartPadding, chartPadding)
        chartBackgroundColor = gt.getColor(R.styleable.PieChart_chartBackground, chartBackgroundColor)
        gt.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun setValues(arr: ArrayList<Double>) {
        values = arr
        valuesSum = 0.0
        values.forEach {
            valuesSum += it
        }
    }

    private fun setColors(colors: ArrayList<Int>) {
        this.colors = colors
    }

    fun setData(values: ArrayList<Double>, colors: ArrayList<Int>) {
        values.forEach {
            if (it < 0.0)
                throw WrongChartDataException("Values in chart cannot be null")
        }
        setValues(values)
        setColors(colors)
        invalidate()
    }

    private fun clearCanvas(canvas: Canvas) {
        paint.color = chartBackgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun emptyValues(canvas: Canvas) {
        // TODO: (handling empty values)
        paint.color = Color.parseColor("#afafaf")
        val message = "Empty"
        paint.textSize = 50f
        canvas.drawText(message, width / 2f - paint.measureText(message) / 2f, height / 2f + 25f, paint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        val chartSize = height - 2 * chartPadding

        clearCanvas(canvas)

        paint.textSize = 30f
        paint.color = Color.parseColor("#ffffff")

        if (valuesSum == 0.0) {
            emptyValues(canvas)
            return
        }

        var prevAngle = 0.0
        values.forEachIndexed { index, it ->
            run {
                var curAngle = Math.PI * 2.0 * it / valuesSum
                curAngle = Math.toDegrees(curAngle)

                paint.color = colors[index % colors.size]

                canvas.drawArc(
                    chartPadding, chartPadding,
                    chartPadding + chartSize, chartPadding + chartSize,
                    prevAngle.toFloat(), curAngle.toFloat(), true, paint
                )

                prevAngle += curAngle
            }
        }

        val ringPxWidth = UnitsHelper.fromDp(ringWidth, ctx.resources.displayMetrics)

        paint.color = chartBackgroundColor
        canvas.drawArc(
            chartPadding + ringPxWidth,
            chartPadding + ringPxWidth,
            chartPadding + chartSize - ringPxWidth,
            chartPadding + chartSize - ringPxWidth,
            0f,
            360f,
            true,
            paint
        )
    }
}