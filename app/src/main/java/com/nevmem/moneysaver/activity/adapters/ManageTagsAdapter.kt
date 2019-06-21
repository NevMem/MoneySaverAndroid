package com.nevmem.moneysaver.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.views.MyExpandableListAdapter
import com.nevmem.moneysaver.views.utils.MyDataObserver
import kotlinx.android.synthetic.main.manage_tags_child_view.view.*
import kotlinx.android.synthetic.main.manage_tags_parent_view.view.*
import javax.inject.Inject

class ManageTagsAdapter(private var ctx: Context, activity: AppCompatActivity, app: App) : MyExpandableListAdapter {
    @Inject
    lateinit var tagsRepo: TagsRepository
    private val data = ArrayList<String>()

    private var mDataObserver: MyDataObserver? = null

    init {
        app.appComponent.inject(this)
        tagsRepo.tags.observe(activity, Observer {
            data.clear()
            it.forEach { value -> data.add(value.name) }
            notifyDataSetChanged()
        })
    }

    private fun notifyDataSetChanged() {
        mDataObserver?.fireNotifications()
    }

    override fun registerDataSetObserver(dataObserver: MyDataObserver) {
        mDataObserver = dataObserver
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.manage_tags_parent_view, parent, false)
        view.header.text = "Manage your tags"
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if (groupPosition != 0)
            throw IllegalArgumentException("groupPosition have to be equals to 0")

        return data.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        if (groupPosition != 0)
            throw IllegalArgumentException("groupPosition have to be equals to 0")

        return data[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        parent: ViewGroup?
    ): View {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.manage_tags_child_view, parent, false)
        view.name.text = getChild(groupPosition, childPosition) as String
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = 1

    override fun getGroup(groupPosition: Int): Any = data
}