package com.nevmem.moneysaver.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log.i
import android.view.View
import java.lang.Integer.max

class BarChart(val cntx: Context, val attrs: AttributeSet): View(cntx, attrs) {
    var values = ArrayList<Float>()
    var showAmount = 20

    init {
        System.out.println("Hello from BarChart init() function")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    fun drawBar(canvas: Canvas?, dx: Int, dy: Int, barWidth: Int, barHeight: Int) {
        val paint = Paint()
        paint.color = Color.parseColor("#FF3C3C")
        canvas!!.drawRect((dx - barWidth / 2).toFloat(),
                (dy - barHeight).toFloat(),
                (dx + barWidth / 2).toFloat(),
                dy.toFloat(),
                paint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.parseColor("#292929")
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (values.size != 0) {
            var index = values.size - showAmount
            if (index < 0)
                index = 0
            var mx = values[index]
            for (i in index until (values.size))
                if (mx < values[i])
                    mx = values[i]
            System.out.println(mx)
            for (i in index until (values.size)) {
                val dx = width * (i - index) / showAmount + width / showAmount / 2
                drawBar(canvas, dx, height - 10, 10, (values[i] * (height - 20) / mx).toInt())
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