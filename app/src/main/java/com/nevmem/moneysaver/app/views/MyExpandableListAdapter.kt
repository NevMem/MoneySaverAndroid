package com.nevmem.moneysaver.app.views

import android.view.View
import android.view.ViewGroup
import com.nevmem.moneysaver.app.views.utils.MyDataObserver

interface MyExpandableListAdapter {
    fun registerDataSetObserver(dataObserver: MyDataObserver)

    fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean

    fun hasStableIds(): Boolean

    fun getGroupView(groupPosition: Int, isExpanded: Boolean, parent: ViewGroup?): View

    fun getChildrenCount(groupPosition: Int): Int

    fun getChild(groupPosition: Int, childPosition: Int): Any

    fun getGroupId(groupPosition: Int): Long

    fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        parent: ViewGroup?
    ): View

    fun getChildId(groupPosition: Int, childPosition: Int): Long

    fun getGroupCount(): Int

    fun getGroup(groupPosition: Int): Any

    fun onAttachedToWindow()
    fun onDetachedFromWindow()
}
