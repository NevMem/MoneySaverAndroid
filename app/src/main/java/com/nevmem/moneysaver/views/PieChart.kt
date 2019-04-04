package com.nevmem.moneysaver.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.exceptions.WrongChartDataException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class PieChart(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private var paint: Paint = Paint(0)
    private var values: ArrayList<Double> = ArrayList()
    private var labels: ArrayList<String> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()
    private var description: String = ""

    private var sortedOrder: ArrayList<Int> = ArrayList()

    private var mul = 0.0
    private var chartPadding = 10.0f // Default value

    private var valuesSum: Double = 0.0

    init {
        paint.flags = paint.flags.or(Paint.ANTI_ALIAS_FLAG)

        val gt = context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0)
        chartPadding = gt.getFloat(R.styleable.PieChart_chartPadding, chartPadding)
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

    private fun setLabels(arr: ArrayList<String>) {
        labels = arr
    }

    private fun setDescription(description: String) {
        this.description = description
    }

    private fun generateColors() {
        colors.clear()
        val random = Random(System.currentTimeMillis()) // TODO: (color generator)
        for (index in 0 until (values.size)) {
            val red = random.nextFloat()
            val green = random.nextFloat()
            val blue = random.nextFloat()
            val cur = -0x1000000 or
                    ((red * 255.0f + 0.5f).toInt() shl 16) or
                    ((green * 255.0f + 0.5f).toInt() shl 8) or
                    (blue * 255.0f + 0.5f).toInt()
            colors.add(cur)
        }
    }

    private fun makeOrder() {
        sortedOrder.clear()
        for (index in 0 until (values.size))
            sortedOrder.add(index)
        Collections.sort(sortedOrder) { first: Int, second: Int -> values[second].compareTo(values[first]) }
    }

    fun setData(values: ArrayList<Double>, labels: ArrayList<String>) {
        if (values.size != labels.size)
            throw WrongChartDataException("Value and labels arrays has unequal sizes")
        setValues(values)
        setLabels(labels)
        generateColors()
        makeOrder()

        invalidate()
    }

    private fun clearCanvas(canvas: Canvas) {
        paint.color = Color.parseColor("#191919")
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
        val descriptionHeight = 70f
        val chartSize = height - 2 * chartPadding - descriptionHeight
        val textLeftBorder: Float = height.toFloat()

        clearCanvas(canvas)

        paint.textSize = 30f
        paint.color = Color.parseColor("#ffffff")
        canvas.drawText(description, chartPadding, descriptionHeight - 10f, paint)

        if (valuesSum == 0.0) {
            emptyValues(canvas)
            return
        }

        var prevAngle = 0.0
        values.forEachIndexed { index, it ->
            run {
                var curAngle = Math.PI * 2.0 * it / valuesSum
                curAngle = Math.toDegrees(curAngle)

                paint.color = colors[index]

                canvas.drawArc(
                    chartPadding, chartPadding + descriptionHeight,
                    chartPadding + chartSize, chartPadding + chartSize + descriptionHeight,
                    prevAngle.toFloat(), curAngle.toFloat(), true, paint
                )

                prevAngle += curAngle
            }
        }

        paint.textSize = 22f

        val percentsWidth = 70f

        for (i in 0 until (sortedOrder.size)) {
            val index = sortedOrder[i]
            paint.color = colors[index]
            canvas.drawText(labels[index], textLeftBorder + percentsWidth, (i + 1) * 34f + descriptionHeight, paint)

            canvas.drawText(
                (values[index] * 100.0 / valuesSum).toInt().toString() + " %",
                textLeftBorder, (i + 1) * 34f + descriptionHeight, paint
            )
        }
    }
}