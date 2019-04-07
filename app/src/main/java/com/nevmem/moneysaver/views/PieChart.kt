package com.nevmem.moneysaver.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log.i
import android.view.View
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.exceptions.WrongChartDataException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class PieChart(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private var paint: Paint = Paint(0)
    private var values: ArrayList<Double> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()

    private var chartBackgroundColor = Color.parseColor("#191919")

    private var mul = 0.0
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

    fun setColors(colors: ArrayList<Int>) {
        this.colors = colors
    }

    fun setData(values: ArrayList<Double>, colors: ArrayList<Int>) {
        setValues(values)
        setColors(colors)
        invalidate()
    }

    private fun clearCanvas(canvas: Canvas) {
        paint.color = chartBackgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    fun emptyValues(canvas: Canvas) {
        // TODO: (handling empty values)
        paint.color = Color.parseColor("#afafaf")
        val message = "There is nothing to show"
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

        paint.color = chartBackgroundColor
        canvas.drawArc(
            chartPadding + 60, chartPadding + 60,
            chartPadding + chartSize - 60, chartPadding + chartSize - 60,
            0f, 360f, true, paint
        )
    }
}