package com.example.performmonitorutil

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.performmonitorutil.MainActivity.Metric


class MetricAdapter(private val metrics: List<Metric>) :
    RecyclerView.Adapter<MetricAdapter.MetricViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetricViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.metric_item, parent, false)
        return MetricViewHolder(view)
    }

    override fun onBindViewHolder(holder: MetricViewHolder, position: Int) {
        val (text1, text2,text3) = metrics[position]
        Log.d("RVAdapter=size1",metrics.size.toString())
        Log.d("RVAdapter", "metrics: ${metrics.toString()}")
        Log.d("RVAdapter=position",position.toString())

        holder.text1.text = text1
        holder.text2.text = text2
        holder.text3.text = text3
    }

    override fun getItemCount(): Int {
        return metrics.size
    }

    class MetricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1: TextView
        var text2: TextView
        var text3: TextView

        init {
            text1 = itemView.findViewById(R.id.text1)
            text2 = itemView.findViewById(R.id.text2)
            text3 = itemView.findViewById(R.id.text3)
        }
    }
}