package com.nevmem.moneysaver.fragments.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.FullDescriptionActivity
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.utils.TransitionsLocker
import com.nevmem.moneysaver.views.ChooseOneFromListDialog
import com.nevmem.moneysaver.views.ConfirmationDialog
import kotlinx.android.synthetic.main.record_layout.view.*
import javax.inject.Inject

class HistoryFragmentAdapter(
    private val activity: Activity,
    private val fragment: HistoryFragment,
    lifeCycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var history: ArrayList<Record> = ArrayList()
    private var filtered: ArrayList<Record> = ArrayList()
    private var animationPosition: Int = 0

    private val transitionsLocker = TransitionsLocker()

    @Inject
    lateinit var tagsRepo: TagsRepository

    @Inject
    lateinit var historyRepo: HistoryRepository

    class ListenableToggleableArray(fromArray: List<String>) {
        private var array = ArrayList<String>()
        private var toggled = ArrayList<Boolean>()
        private var listener: (() -> Unit)? = null

        init {
            fromArray.forEach {
                array.add(it)
                toggled.add(false)
            }
        }

        fun enable(str: String) {
            val index = array.indexOf(str)
            if (index == -1)
                return
            toggled[index] = true
            updated()
        }

        fun disable(str: String) {
            val index = array.indexOf(str)
            if (index == -1)
                return
            toggled[index] = false
            updated()
        }

        fun getToggled(): List<String> {
            val res = ArrayList<String>()
            for (i in 0 until (array.size)) {
                if (toggled[i]) {
                    res.add(array[i])
                }
            }
            return res
        }

        fun getUnToggled(): List<String> {
            val res = ArrayList<String>()
            for (i in 0 until (array.size)) {
                if (!toggled[i]) {
                    res.add(array[i])
                }
            }
            return res
        }

        private fun updated() {
            listener?.invoke()
        }

        fun setListener(cb: () -> Unit) {
            listener = cb
        }
    }

    var filter = ""
        set(value) {
            if (field == value) return
            field = value
            applyFilter()
        }
    private var filterTag: ListenableToggleableArray

    init {
        (activity.application as App).appComponent.inject(this)
        historyRepo.history.observe(lifeCycleOwner, Observer {
            history = it
            applyFilter()
            i("HFA", "Changes observed")
        })
        filterTag = ListenableToggleableArray(tagsRepo.getTagsAsList())
        filterTag.setListener {
            applyFilter()
        }
    }

    enum class ViewHolderType(val type: Int) {
        HEADER(0), ELEMENT(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewHolderType.HEADER.type -> {
                val header = LayoutInflater.from(parent.context).inflate(R.layout.history_page_header, parent, false)
                HeaderViewHolder(activity, header, filter, filterTag)
            }
            else -> {
                val element = LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
                ElementViewHolder(element)
            }
        }
    }

    private fun deleteRecord(record: Record) {
        historyRepo.delete(record)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder.itemViewType == ViewHolderType.HEADER.type -> {
                val header = holder as HeaderViewHolder
                header.headerText.text = "Browse your outcomes"
                header.searchFiled.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        filter = s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                holder.itemView.setOnClickListener {}
            }
            holder.itemViewType == ViewHolderType.ELEMENT.type -> {
                with(holder as ElementViewHolder) {
                    recordName.text = filtered[position - 1].name
                    recordValue.text = filtered[position - 1].value.toString()
                    recordTag.text = filtered[position - 1].tag
                    recordDate.text = filtered[position - 1].date.toString()

                    itemView.setOnClickListener {
                        if (position - 1 in 0 until(filtered.size))
                            openFullDescriptionActivity(it, filtered[position - 1].id)
                    }
                }
            }
        }
        if (animationPosition < position) {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.cascade_anim)
            holder.itemView.startAnimation(animation)
            animationPosition = position
        }
    }

    private fun openFullDescriptionActivity(view: View, id: String) {
        if (!transitionsLocker.canRunTransition()) return
        transitionsLocker.lockTransitions()
        val intent = Intent(activity, FullDescriptionActivity::class.java)
        intent.putExtra("id", id)
        view.card.transitionName = "descriptionPageEnterTransition"
        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            android.util.Pair<View, String>(view.card, "descriptionPageEnterTransition")
        )
        fragment.startActivityForResult(intent, HistoryFragment.FULL_DESCRIPTION_PAGE_CALL, options.toBundle())
    }

    fun handleReturn() {
        transitionsLocker.unlockTransitions()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return ViewHolderType.HEADER.type
        return ViewHolderType.ELEMENT.type
    }

    override fun getItemCount(): Int {
        return filtered.size + 1
    }

    private fun getRealPosition(position: Int): Int {
        if (position !in 1 .. filtered.size + 1)
            throw IllegalArgumentException("Wrong position $position, have to be from 1 and till ${filtered.size}")
        return position - 1
    }

    fun handleRemoveItem(position: Int) {
        val realPosition = getRealPosition(position)
        val record = filtered[realPosition]
        deleteRecord(record)
    }

    class HeaderViewHolder(ctx: Context, view: View, filter: String, tags: ListenableToggleableArray) :
        RecyclerView.ViewHolder(view) {
        val headerText: TextView = view.findViewById(R.id.history_fragment_header_text)
        val searchFiled: EditText = view.findViewById(R.id.searchField)
        private val chipGroup: ChipGroup = view.findViewById(R.id.chooseTags)

        init {
            searchFiled.setText(filter)
            val addTag = Chip(ctx)
            addTag.text = "+ Add"
            chipGroup.addView(addTag)
            addTag.setOnClickListener {
                val dialog = ChooseOneFromListDialog(ctx, "Choose tag for filter", tags.getUnToggled())
                val popup =
                    PopupWindow(dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                popup.showAtLocation(headerText, Gravity.CENTER, 0, 0)
                dialog.setOkListener {
                    tags.enable(it)
                    val chip = Chip(ctx)
                    chip.text = it
                    chip.isCheckable = false
                    chip.isCloseIconVisible = true
                    chip.height = ViewGroup.LayoutParams.MATCH_PARENT
                    chip.width = ViewGroup.LayoutParams.MATCH_PARENT
                    chip.setOnCloseIconClickListener { _ ->
                        run {
                            chipGroup.removeView(chip)
                            tags.disable(it)
                        }
                    }
                    chipGroup.addView(chip)
                    popup.dismiss()
                }
                dialog.setDismissListener {
                    popup.dismiss()
                }
            }
        }
    }

    class ElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordName: TextView = view.findViewById(R.id.recordNameField)
        val recordDate: TextView = view.findViewById(R.id.dateField)
        val recordValue: TextView = view.findViewById(R.id.recordValue)
        val recordTag: TextView = view.findViewById(R.id.tagField)
    }


    /**
     * Checks if suspect is a sub sequence of array
     * returns null if NOT
     * returns ArrayList of Ints which to remove to make array be equals to suspect
     */
    private fun isSubSequence(array: ArrayList<Record>, suspect: ArrayList<Record>): ArrayList<Int>? {
        var top = 0
        val indices = ArrayList<Int>()
        for (i in 0 until array.size) {
            if (top < suspect.size && array[i] == suspect[top]) {
                top += 1
            } else {
                indices.add(i)
            }
        }
        if (top != suspect.size) return null
        return indices
    }

    private fun applyFilter() {
        val before = filtered

        val currentFiltered = ArrayList<Record>()
        val tags = filterTag.getToggled()
        if (filter.isNotEmpty()) {
            for (i in 0 until (history.size)) {
                if (history[i].name.toLowerCase().indexOf(filter.toLowerCase()) != -1 && (tags.isEmpty() || history[i].tag in tags)) {
                    currentFiltered.add(history[i])
                }
            }
        } else {
            history.forEach {
                if (tags.isEmpty() || it.tag in tags)
                    currentFiltered.add(it)
            }
        }
        filtered = currentFiltered

        val indices = isSubSequence(before, filtered)

        if (indices == null) {
            notifyDataSetChanged()
        } else {
            indices.forEach {
                notifyItemRemoved(it)
            }
        }
    }
}