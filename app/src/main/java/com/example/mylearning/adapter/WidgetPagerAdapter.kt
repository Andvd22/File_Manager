package com.example.mylearning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R

class WidgetPagerAdapter : RecyclerView.Adapter<WidgetPagerAdapter.PageViewHolder>(){
    private val layouts = listOf(
        R.layout.widget_preview_1,
        R.layout.widget_preview_2,
        R.layout.widget_preview_3
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PageViewHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int {
        return layouts.size
    }

    override fun getItemViewType(position: Int): Int {
        return layouts[position]
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}