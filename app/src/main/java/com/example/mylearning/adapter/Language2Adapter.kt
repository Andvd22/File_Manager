package com.example.mylearning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.databinding.ItemLanguage2Binding
import com.example.mylearning.model.Language2Item

class Language2Adapter(private val onItemClick: (Language2Item) -> Unit): ListAdapter<Language2Item, Language2Adapter.ItemViewHolder>(
    Language2Adapter.ItemDiffCallback
){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding = ItemLanguage2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        return holder.bind(getItem(position))
    }

    companion object ItemDiffCallback: DiffUtil.ItemCallback<Language2Item>(){
        override fun areItemsTheSame(
            oldItem: Language2Item,
            newItem: Language2Item
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Language2Item,
            newItem: Language2Item
        ): Boolean {
            return oldItem == newItem
        }

    }
    inner class ItemViewHolder(private val binding: ItemLanguage2Binding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onItemClick(getItem(position))
                }
            }
        }
        fun bind(item: Language2Item){
            binding.apply {
                binding.root.isSelected = item.isSelected
                ivFlag.setImageResource(item.flagRes)
                tvLanguageName.text = item.name
                binding.radioButton.visibility = if(item.isSelected) View.GONE else View.VISIBLE
                binding.radioSelectButton.visibility = if(item.isSelected) View.VISIBLE else View.GONE
            }
        }
    }
}