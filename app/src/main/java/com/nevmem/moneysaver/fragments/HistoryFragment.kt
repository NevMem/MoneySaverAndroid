package com.nevmem.moneysaver.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log.i
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.fragments.adapters.HistoryFragmentAdapter
import kotlinx.android.synthetic.main.history_layout.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt


class HistoryFragment : Fragment() {
    companion object {
        const val FULL_DESCRIPTION_PAGE_CALL = 0
        const val SWIPE_THRESHOLD = 250
        const val SWIPE_CIRCLE_DX = 60
    }

    lateinit var app: App

    @Inject
    lateinit var historyRepo: HistoryRepository

    private lateinit var adapter: HistoryFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("HistoryFragment", "onCreate")
        try {
            app = activity!!.applicationContext as App
        } catch (_: KotlinNullPointerException) {
            println("Null pointer")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historySwipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(context!!, R.color.themeColor),
            ContextCompat.getColor(context!!, R.color.specialColor),
            ContextCompat.getColor(context!!, R.color.errorColor)
        )
        historySwipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context!!,
                R.color.backgroundColor
            )
        )

        app.appComponent.inject(this)

        historySwipeRefreshLayout.setOnRefreshListener {
            historyRepo.tryUpdate()
        }
        historyRepo.loading.observe(this, Observer {
            if (it != null) {
                historySwipeRefreshLayout.isRefreshing = it
            }
        })

        adapter = HistoryFragmentAdapter(activity!!, this, this)
        val itemTouchHelper = ItemTouchHelper(SwipeController(activity!!, adapter))
        itemTouchHelper.attachToRecyclerView(wrapper)
        wrapper.adapter = adapter
        val layoutManager = LinearLayoutManager(wrapper.context)
        wrapper.layoutManager = layoutManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("On activity result: $requestCode")
        if (requestCode == FULL_DESCRIPTION_PAGE_CALL) {
            adapter.handleReturn()
        }
    }

    class SwipeController(private var ctx: Context, private var mAdapter: HistoryFragmentAdapter) :
        ItemTouchHelper.Callback() {
        private var swipeBack: Boolean = false

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            if (viewHolder is HistoryFragmentAdapter.ElementViewHolder)
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            return makeMovementFlags(0, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.LEFT) {
                mAdapter.handleRemoveItem(viewHolder.adapterPosition)
            }
        }

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            if (swipeBack) {
                swipeBack = false
                return 0
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setTouchListener(
            recyclerView: RecyclerView, type: Boolean
        ) {
            recyclerView.setOnTouchListener { _, event ->
                run {
                    if (type) {
                        swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                    }
                    false
                }
            }
        }

        private fun interpolate(t: Float): Float {
            val x = abs(t)
            if (x >= SWIPE_THRESHOLD - SWIPE_CIRCLE_DX) {
                return if (x > SWIPE_THRESHOLD + SWIPE_CIRCLE_DX) 1f else (x - SWIPE_THRESHOLD + SWIPE_CIRCLE_DX) / (2f * SWIPE_CIRCLE_DX)
            }
            return 0f
        }

        private fun drawCircle(c: Canvas, dX: Float, v: View, paint: Paint) {
            val width = v.width
            val height = v.height
            val maxRadius = sqrt(width * width + height * height / 4f)

            val currentRadius = maxRadius * interpolate(dX)

            paint.color = ContextCompat.getColor(ctx, R.color.backgroundColor)
            c.drawRect(v.left.toFloat(), v.top.toFloat(), v.right.toFloat(), v.bottom.toFloat(), paint)

            if (currentRadius > 0) {
                val colors = arrayOf(
                    ContextCompat.getColor(ctx, R.color.deleteFromHistoryColor),
                    ContextCompat.getColor(ctx, R.color.deleteFromHistoryColor),
                    ContextCompat.getColor(ctx, R.color.backgroundColor)
                ).toIntArray()
                val stops = arrayOf(0f, interpolate(dX), interpolate(dX) + 0.001f).toFloatArray()

                val shader = RadialGradient(
                    v.left + width - 90f,
                    v.top + height / 2f,
                    maxRadius,
                    colors,
                    stops,
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                c.drawRect(v.left.toFloat(), v.top.toFloat(), v.right.toFloat(), v.bottom.toFloat(), paint)
                paint.shader = null
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            c.save()
            if (actionState == ACTION_STATE_SWIPE)
                setTouchListener(recyclerView, dX >= -SWIPE_THRESHOLD)
            val item = viewHolder.itemView

            val paint = Paint()
            paint.flags = Paint.ANTI_ALIAS_FLAG

            drawCircle(c, dX, item, paint)
            paint.color = ContextCompat.getColor(ctx, R.color.default_white_color)
            paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, ctx.resources.displayMetrics)
            c.drawText("Delete", item.right - 140f, item.top + item.paddingTop + item.height / 2f + 18f, paint)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            c.restore()
        }
    }
}