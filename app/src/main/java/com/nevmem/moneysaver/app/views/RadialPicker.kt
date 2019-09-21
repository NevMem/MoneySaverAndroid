package com.nevmem.moneysaver.app.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log.i
import android.view.View
import androidx.core.content.ContextCompat
import com.nevmem.moneysaver.R


class RadialPicker(private var ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private val tag = "RADIAL_PICKER"

    private var valueChangedListener: ((Int) -> Unit)? = null

    private var defaultPadLeft = 20.0f
    private var defaultPadBottom = 20.0f
    private var defaultPadRight = 20.0f
    private var defaultPadTop = 20.0f

    private val paint = Paint()
    private var padRight = 10.0f
    private var padTop = 10.0f
    private var padLeft = 10.0f
    private var padBottom = 10.0f
    private var size = 0.0f

    private var centerX = 0.0f
    private var centerY = 0.0f

    private var numbersSize = 24f
    private var pickedRadius = 26f

    var value: Int = -1
        set(value) {
            i(tag, "Setting value $value")
            if (field == value) return
            field = value
            invalidate()
            valueChangedListener?.invoke(field)
        }

    init {
        paint.flags = Paint.ANTI_ALIAS_FLAG

        setOnTouchListener { _, event ->
            run {
                var dx = event.x - centerX
                var dy = event.y - centerY
                val ln = Math.sqrt(dx * dx.toDouble() + dy * dy).toFloat()
                dx /= ln
                dy /= ln
                val scalarProd = -dy.toDouble()
                val crossProd = dx * (1.0)
                var angle = Math.atan2(crossProd, scalarProd)
                if (angle <= 0.0)
                    angle += Math.PI * 2
                value = ((angle + Math.PI / 24) / (2 * Math.PI) * 12).toInt() % 12
                true
            }
        }
    }

    fun setOnValueChangedListener(cb: (Int) -> Unit) {
        valueChangedListener = cb
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun fillBackground(canvas: Canvas) {
        paint.color = Color.parseColor("#000000")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun fillCircle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(ctx, R.color.cardColor)
        canvas.drawArc(
            padLeft,
            padTop,
            width - padRight,
            height - padBottom,
            0f,
            Math.toDegrees(Math.PI * 2).toFloat(),
            true,
            paint
        )
    }

    private fun drawNumbers(canvas: Canvas) {
        val radius = size / 2 - numbersSize
        paint.color = Color.parseColor("#a0ffffff")
        paint.textSize = numbersSize
        for (i in 0 until (12)) {
            val alpha = Math.PI / 6 * i
            val x = Math.sin(alpha).toFloat() * radius
            val y = -Math.cos(alpha).toFloat() * radius
            val bounds = Rect()
            var drawValue = i.toString()
            if (i == 0)
                drawValue = 12.toString()
            paint.getTextBounds(drawValue, 0, drawValue.length, bounds)
            canvas.drawText(
                drawValue,
                centerX + x - bounds.width() / 2,
                centerY + y + bounds.height() / 2,
                paint
            )
        }
    }

    private fun drawPicked(canvas: Canvas) {
        if (value in 0..11) {
            paint.color = ContextCompat.getColor(ctx, R.color.themeColor)
            val radius = size / 2 - numbersSize
            val alpha = Math.PI / 6 * value
            val x = Math.sin(alpha).toFloat() * radius
            val y = -Math.cos(alpha).toFloat() * radius
            val positionX = centerX + x
            val positionY = centerY + y
            i(tag, "$positionX $positionY")
            canvas.drawArc(
                positionX - pickedRadius,
                positionY - pickedRadius,
                positionX + pickedRadius,
                positionY + pickedRadius,
                0f,
                Math.toDegrees(Math.PI * 2).toFloat(),
                true,
                paint
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        size = Math.min(width - defaultPadLeft - defaultPadRight, height - defaultPadTop - defaultPadBottom)

        padLeft = width - size - defaultPadRight
        padBottom = height - size - defaultPadBottom
        padTop = defaultPadTop
        padRight = defaultPadRight

        centerX = padLeft + size / 2
        centerY = padTop + size / 2

        fillBackground(canvas)
        fillCircle(canvas)

        drawPicked(canvas)
        drawNumbers(canvas)
    }
}