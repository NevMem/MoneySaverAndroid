package com.nevmem.moneysaver.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.nevmem.moneysaver.app.views.utils.MyDataObserver

class MyExpandableList : LinearLayout {
    private var mAdapter: MyExpandableListAdapter? = null
    private var mExpanded = ArrayList<Boolean>()

    private var groupViewsMinimized = ArrayList<View>()
    private var groupViewsExpanded = ArrayList<View>()
    private var children = ArrayList<ArrayList<View>>()

    private var mDataObserver = MyDataObserver()

    private val mContext: Context

    constructor(ctx: Context) : super(ctx) {
        mContext = ctx
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        mContext = ctx
    }

    init {
        orientation = VERTICAL
        mDataObserver.observe {
            children.forEach {
                it.forEach { view ->
                    removeView(view)
                }
            }
            groupViewsMinimized.forEach {
                removeView(it)
            }
            groupViewsExpanded.forEach {
                removeView(it)
            }
            children.clear()
            groupViewsExpanded.clear()
            groupViewsMinimized.clear()
            revalidateSize()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAdapter?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAdapter?.onDetachedFromWindow()
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    private fun getGroupView(index: Int, isExpanded: Boolean): View {
        return when (isExpanded) {
            true -> groupViewsExpanded[index]
            false -> groupViewsMinimized[index]
        }
    }

    private fun trigger(index: Int) {
        mExpanded[index] = !mExpanded[index]
        getGroupView(index, !mExpanded[index]).visibility = View.GONE
        getGroupView(index, mExpanded[index]).visibility = View.VISIBLE
        children[index].forEach {
            if (mExpanded[index]) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    private fun revalidateSize() {
        mAdapter?.let {
            val size = it.getGroupCount()
            while (mExpanded.size < size) {
                mExpanded.add(false)
            }
            for (index in groupViewsMinimized.size until (size)) {
                var child = it.getGroupView(index, false, this)
                child.setOnClickListener {
                    trigger(index)
                }
                groupViewsMinimized.add(child)
                addView(child)

                child = it.getGroupView(index, true, this)
                child.isActivated = true
                child.visibility = View.GONE
                child.setOnClickListener {
                    trigger(index)
                }
                groupViewsExpanded.add(child)
                addView(child)

                val current = ArrayList<View>()
                for (i in 0 until (it.getChildrenCount(index))) {
                    val innerChild = it.getChildView(index, i, i == it.getChildrenCount(index) - 1, this)
                    current.add(innerChild)
                    innerChild.visibility = View.GONE
                    addView(innerChild)
                }
                children.add(current)
            }
        }
    }

    fun setAdapter(adapter: MyExpandableListAdapter) {
        mAdapter = adapter
        mAdapter?.registerDataSetObserver(mDataObserver)
    }
}
