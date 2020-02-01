package com.nevmem.moneysaver.app.activity.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.utils.TypeUtils
import com.nevmem.moneysaver.app.views.PieChart
import kotlinx.android.synthetic.main.large_lable_row.view.*

class MonthDescriptionLabelsAdapter(private var ctx: Context) :
    RecyclerView.Adapter<MonthDescriptionLabelsAdapter.VH>() {
    data class LabelRepresentation(val name: String, val value: Double, val color: Int)

    private var data: ArrayList<LabelRepresentation> = ArrayList()

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(ctx).inflate(R.layout.large_lable_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.name.text = data[position].name
        holder.badge.background.colorFilter =
            PorterDuffColorFilter(data[position].color, PorterDuff.Mode.MULTIPLY)
        holder.value.text = TypeUtils.formatDouble(data[position].value)
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val badge: ImageView = view.labelBadge
        val name: TextView = view.labelName
        var value: TextView = view.labelValue
    }

    @SuppressLint("UseSparseArrays")
    fun changeData(map: HashMap<String, Double>) {
        val newData = ArrayList<LabelRepresentation>()
        for (element in map) {
            newData.add(LabelRepresentation(element.key, element.value, PieChart.baseColors[newData.size % PieChart.baseColors.size]))
        }
        newData.sortBy { element -> element.value }
        newData.reverse()
        val before = data
        data = newData
        var isSubSequence = false
        var top = 0
        for (i in 0 until before.size) {
            if (top < data.size && data[top].name == before[i].name) {
                top += 1
            }
            if (top == data.size) {
                isSubSequence = true
            }
        }

        if (isSubSequence) {
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
