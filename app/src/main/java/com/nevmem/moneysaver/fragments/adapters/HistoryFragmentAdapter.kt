package com.nevmem.moneysaver.fragments.adapters

import android.app.Activity
import android.app.ActivityOptions
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
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.FullDescriptionActivity
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.utils.TransitionsLocker
import com.nevmem.moneysaver.views.ConfirmationDialog
import kotlinx.android.synthetic.main.history_page_header.view.*
import kotlinx.android.synthetic.main.record_layout.view.*


class HistoryFragmentAdapter(
    private val activity: Activity,
    private val fragment: HistoryFragment,
    lifeCycleOwner: LifecycleOwner,
    private val historyRepo: HistoryRepository
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var history: ArrayList<Record> = ArrayList()
    private var filtered: ArrayList<Record> = ArrayList()
    private var animationPosition: Int = 0

    private val transitionsLocker = TransitionsLocker()

    var filter = ""
        set(value) {
            if (field == value) return
            field = value
            applyFilter()
        }

    init {
        historyRepo.history.observe(lifeCycleOwner, Observer {
            history = it
            applyFilter()
            i("HFA", "Changes observed")
        })
    }

    enum class ViewHolderType(val type: Int) {
        HEADER(0), ELEMENT(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewHolderType.HEADER.type -> {
                val header = LayoutInflater.from(parent.context).inflate(R.layout.history_page_header, parent, false)
                HeaderViewHolder(header, filter)
            }
            else -> {
                val element = LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
                ElementViewHolder(element)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder.itemViewType == ViewHolderType.HEADER.type -> {
                val header = holder as HeaderViewHolder
                header.headerText.text = "Browse your outcomes"
                header.searchFiled.addTextChangedListener(object: TextWatcher {
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
                    recordWallet.text = filtered[position - 1].wallet
                    recordDate.text = filtered[position - 1].date.toString()
                    deleteButton.setOnClickListener {
                        val popupView = ConfirmationDialog(activity, "Do you really want delete this record?")
                        val popup = PopupWindow(
                            popupView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        popup.showAtLocation(itemView, Gravity.CENTER, 0, 0)
                        popupView.setOkListener {
                            popup.dismiss()
                            historyRepo.delete(filtered[position - 1])
                        }

                        popupView.setDismissListener {
                            popup.dismiss()
                        }
                    }

                    itemView.setOnClickListener {
                        openFullDescriptionActivity(it, position - 1)
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

    private fun openFullDescriptionActivity(view: View, index: Int) {
        if (!transitionsLocker.canRunTransition()) return
        transitionsLocker.lockTransitions()
        val intent = Intent(activity, FullDescriptionActivity::class.java)
        intent.putExtra("index", index)
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

    class HeaderViewHolder(view: View, filter: String) : RecyclerView.ViewHolder(view) {
        val headerText: TextView = view.findViewById(R.id.history_fragment_header_text)
        val searchFiled: EditText = view.findViewById(R.id.searchField)
        init {
            searchFiled.setText(filter)
        }
    }

    class ElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordName: TextView = view.findViewById(R.id.recordNameField)
        val recordDate: TextView = view.findViewById(R.id.dateField)
        val recordValue: TextView = view.findViewById(R.id.recordValue)
        val recordWallet: TextView = view.findViewById(R.id.walletField)
        val deleteButton: ImageView = view.findViewById(R.id.deleteRecordButton)
    }

    private fun applyFilter() {
        val currentFiltered = ArrayList<Record>()
        if (filter.isNotEmpty()) {
            for (i in 0 until (history.size)) {
                if (history[i].name.toLowerCase().indexOf(filter.toLowerCase()) != -1)
                    currentFiltered.add(history[i])
            }
        } else {
            history.forEach {
                currentFiltered.add(it)
            }
        }
        filtered = currentFiltered
        notifyDataSetChanged()
    }
}