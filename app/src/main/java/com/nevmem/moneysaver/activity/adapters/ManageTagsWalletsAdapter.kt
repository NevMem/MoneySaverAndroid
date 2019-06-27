package com.nevmem.moneysaver.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.data.repositories.WalletsRepository
import com.nevmem.moneysaver.data.util.ErrorState
import com.nevmem.moneysaver.data.util.LoadingState
import com.nevmem.moneysaver.data.util.NoneState
import com.nevmem.moneysaver.data.util.RequestState
import com.nevmem.moneysaver.views.MyExpandableListAdapter
import com.nevmem.moneysaver.views.utils.MyDataObserver
import kotlinx.android.synthetic.main.manage_tags_child_view.view.*
import kotlinx.android.synthetic.main.manage_tags_parent_view.view.*
import javax.inject.Inject

class ManageTagsWalletsAdapter(private var ctx: Context, private var activity: AppCompatActivity, app: App) :
    MyExpandableListAdapter {
    @Inject
    lateinit var tagsRepo: TagsRepository

    @Inject
    lateinit var walletsRepo: WalletsRepository

    private val tags = ArrayList<String>()
    private val wallets = ArrayList<String>()

    private var mDataObserver: MyDataObserver? = null

    init {
        app.appComponent.inject(this)
        tagsRepo.tags.observe(activity, Observer {
            tags.clear()
            it.forEach { value -> tags.add(value.name) }
            notifyDataSetChanged()
        })
        walletsRepo.wallets.observe(activity, Observer {
            wallets.clear()
            it.forEach { value -> wallets.add(value.name) }
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
        if (groupPosition == 0)
            view.header.text = "Tags"
        else if (groupPosition == 1)
            view.header.text = "Wallets"
        view.specialInfo.text = "click to remove"
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        validateGroupPosition(groupPosition)
        if (groupPosition == 0) return tags.size
        return wallets.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        validateGroupPosition(groupPosition)
        if (groupPosition == 0) return tags[childPosition]
        return wallets[childPosition]
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

        view.setOnClickListener {
            processOnChildClick(it, groupPosition, childPosition)
        }
        return view
    }

    private fun validateGroupPosition(groupPosition: Int) {
        if (groupPosition !in 0..2)
            throw IllegalArgumentException("Wrong groupPosition: $groupPosition")
    }

    private fun processOnChildClick(v: View?, groupPosition: Int, childPosition: Int) {
        v?.let { view ->
            run {
                var liveData: LiveData<RequestState>? = null
                if (groupPosition == 0) {
                    if (childPosition in 0..tags.size) {
                        liveData = tagsRepo.delete(tags[childPosition])
                    }
                } else {
                    if (childPosition in 0..wallets.size) {
                        liveData = walletsRepo.delete(wallets[childPosition])
                    }
                }
                liveData?.let { liveDataToObserve ->
                    run {
                        liveDataToObserve.observe(activity, Observer {
                            when (it) {
                                null, NoneState -> {
                                    view.process.visibility = View.GONE
                                }
                                is LoadingState -> {
                                    view.process.text = "Removing..."
                                    view.process.visibility = View.VISIBLE
                                    view.process.setTextColor(
                                        ContextCompat.getColor(
                                            ctx,
                                            R.color.manage_tags_loading_state
                                        )
                                    )
                                }
                                is ErrorState -> {
                                    view.process.text = "Error"
                                    view.process.visibility = View.VISIBLE
                                    view.process.setTextColor(ContextCompat.getColor(ctx, R.color.errorColor))
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = 2

    override fun getGroup(groupPosition: Int): Any {
        validateGroupPosition(groupPosition)
        if (groupPosition == 0) return tags
        return wallets
    }
}