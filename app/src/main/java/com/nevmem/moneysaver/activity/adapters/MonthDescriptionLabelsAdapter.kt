package com.nevmem.moneysaver.activity.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.views.PieChart
import kotlinx.android.synthetic.main.large_lable_row.view.*

class MonthDescriptionLabelsAdapter(private var ctx: Context) :
    RecyclerView.Adapter<MonthDescriptionLabelsAdapter.VH>() {
    data class LabelRepr(val name: String, val value: Double, val color: Int)

    private var data: ArrayList<LabelRepr> = ArrayList()

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(ctx).inflate(R.layout.large_lable_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.name.text = data[position].name
        holder.badge.background.setColorFilter(data[position].color, PorterDuff.Mode.MULTIPLY)
        holder.value.text = data[position].value.toString()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val badge: ImageView = view.labelBadge
        val name: TextView = view.labelName
        var value: TextView = view.labelValue
    }

    @SuppressLint("UseSparseArrays")
    fun changeData(map: HashMap<String, Double>) {
        val newData = ArrayList<LabelRepr>()
        for (element in map) {
            newData.add(LabelRepr(element.key, element.value, PieChart.baseColors[newData.size % PieChart.baseColors.size]))
        }
        newData.sortBy { element -> element.value }
        newData.reverse()
        val before = data
        data = newData
        var isSubsequence = false
        var top = 0
        for (i in 0 until before.size) {
            if (top < data.size && data[top].name == before[i].name) {
                top += 1
            }
            if (top == data.size) {
                isSubsequence = true
            }
        }

        if (isSubsequence) {
            top = 0
            var removed = 0
            for (i in 0 until before.size) {
                if (top < data.size && data[top].name == before[i].name) {
                    if (data[top] != before[i]) {
                        notifyItemChanged(i - removed)
                    }
                    top += 1
                } else {
                    notifyItemRemoved(i - removed)
                    removed += 1
                }
            }
        } else {
            notifyDataSetChanged()
        }
    }
}