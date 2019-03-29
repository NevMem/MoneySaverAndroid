package com.nevmem.moneysaver.fragments.adapters

import android.content.Context
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.views.ConfirmationDialog


class HistoryFragmentAdapter(
    private val context: Context,
    private val lifeCycleOwner: LifecycleOwner,
    private val historyRepo: HistoryRepository
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var history: ArrayList<Record> = ArrayList()
    private var animationPosition: Int = 0

    init {
        historyRepo.history.observe(lifeCycleOwner, Observer {
            history = it
            i("HFA", "Changes observed")
            notifyDataSetChanged()
        })
    }

    enum class ViewHolderType(val type: Int) {
        HEADER(0), ELEMENT(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when {
            viewType == ViewHolderType.HEADER.type -> {
                val header = LayoutInflater.from(parent.context).inflate(R.layout.history_page_header, parent, false)
                return HeaderViewHolder(header)
            }
            else -> {
                val header = LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
                return ElementViewHolder(header)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder.itemViewType == ViewHolderType.HEADER.type -> {
                val header = holder as HeaderViewHolder
                header.headerText.text = "Browse your spendings"
            }
            holder.itemViewType == ViewHolderType.ELEMENT.type -> {
                with(holder as ElementViewHolder) {
                    recordName.text = history[position - 1].name
                    recordValue.text = history[position - 1].value.toString()
                    recordWallet.text = history[position - 1].wallet
                    recordDate.text = history[position - 1].date.toString()
                    deleteButton.setOnClickListener {
                        val popupView = ConfirmationDialog(context, "Do you really want delete this record?")
                        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        popup.showAtLocation(itemView, Gravity.CENTER, 0, 0)
                        popupView.setOkListener {
                            popup.dismiss()
                            historyRepo.delete(history[position - 1])
                        }

                        popupView.setDismissListener {
                            popup.dismiss()
                        }
                    }
                }
            }
        }
        if (animationPosition < position) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            holder.itemView.startAnimation(animation)
            animationPosition = position
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return ViewHolderType.HEADER.type
        return ViewHolderType.ELEMENT.type
    }

    override fun getItemCount(): Int {
        i("HFA", "Size was asked")
        return history.size + 1
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText = view.findViewById<TextView>(com.nevmem.moneysaver.R.id.history_fragment_header_text)
    }

    class ElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordName = view.findViewById<TextView>(R.id.recordNameField)
        val recordDate = view.findViewById<TextView>(R.id.dateField)
        val recordValue = view.findViewById<TextView>(R.id.recordValue)
        val recordWallet = view.findViewById<TextView>(R.id.walletField)
        val deleteButton = view.findViewById<ImageView>(R.id.deleteRecordButton)
    }
}