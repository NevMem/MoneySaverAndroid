package com.nevmem.moneysaver.activity.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.views.PieChart
import kotlinx.android.synthetic.main.large_lable_row.view.*

class MonthDescriptionLabelsAdapter(private var ctx: Context, map: HashMap<String, Double>) :
    RecyclerView.Adapter<MonthDescriptionLabelsAdapter.VH>() {
    data class LabelRepr(val name: String, val value: Double, val color: Int)

    private var data: ArrayList<LabelRepr> = ArrayList()

    init {
        for (element in map) {
            data.add(LabelRepr(element.key, element.value, PieChart.baseColors[data.size % PieChart.baseColors.size]))
        }
        data.sortBy { value -> value.value }
        data.reverse()
    }

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
        val badge = view.labelBadge
        val name = view.labelName
        var value = view.labelValue
    }
}