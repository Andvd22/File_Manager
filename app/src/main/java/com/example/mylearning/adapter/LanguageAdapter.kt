package com.example.mylearning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R
import com.example.mylearning.databinding.ItemLanguageBinding
import com.example.mylearning.model.ChildPosition
import com.example.mylearning.model.LanguageItem

class LanguageAdapter(
    private val onItemClick: (LanguageItem) -> Unit
) : ListAdapter<LanguageItem, LanguageAdapter.LanguageViewHolder>(LanguageDiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class LanguageViewHolder(
        private val binding: ItemLanguageBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onItemClick(getItem(position))
                }
            }
        }
        fun bind(item: LanguageItem) {
            binding.root.isSelected = item.isSelected
            binding.root.setBackgroundResource(backgroundFor(item))
            adjustVerticalMargins(item)
            binding.apply {
                when (item) {
                    is LanguageItem.System -> {
                        ivFlag.setImageResource(R.drawable.flag_system)
                        tvLanguageName.text = item.name
                        hideArrows()
                        showRadio(item.isSelected)
                    }

                    is LanguageItem.Parent -> {
                        // Parent - ngôn ngữ chính
                        ivFlag.setImageResource(item.flagRes)
                        tvLanguageName.text = item.name
                        if (item.hasChildren) {
                            showArrows(item.isExpanded)
                            radioSelectButton.visibility = View.GONE
                            radioButton.visibility = View.GONE
                        } else {
                            hideArrows()
                            showRadio(item.isSelected)
                        }
                    }

                    is LanguageItem.Child -> {
                        // Child - ngôn ngữ con (có thể thêm margin left để indent)
                        ivFlag.setImageResource(item.flagRes)
                        tvLanguageName.text = item.name
                        hideArrows()
                        showRadio(item.isSelected)
                    }
                }
            }
        }
        private fun hideArrows() {
            binding.ivExpandArrowDown.visibility = View.GONE
            binding.ivExpandArrowUp.visibility = View.GONE
        }

        private fun showArrows(isExpanded: Boolean) {
            binding.ivExpandArrowDown.visibility = if (isExpanded) View.GONE else View.VISIBLE
            binding.ivExpandArrowUp.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        private fun showRadio(isSelected: Boolean) {
            binding.radioSelectButton.visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.radioButton.visibility = if (isSelected) View.GONE else View.VISIBLE
        }

        private fun adjustVerticalMargins(item: LanguageItem) {
            val params = binding.root.layoutParams as? ViewGroup.MarginLayoutParams ?: return
            val (topMargin, bottomMargin) = when (item) {
                is LanguageItem.Parent -> {
                    val bottom = if (item.hasChildren && item.isExpanded) 0 else dpToPx(8f)
                    dpToPx(8f) to bottom
                }
                is LanguageItem.Child -> {
                    val top = if (item.position == ChildPosition.FIRST) dpToPx(8f) else 0
                    val bottom = if (item.position == ChildPosition.LAST) dpToPx(8f) else 0
                    top to bottom
                }
                else -> dpToPx(0f) to dpToPx(8f)
            }
            if (params.topMargin != topMargin || params.bottomMargin != bottomMargin) {
                params.topMargin = topMargin
                params.bottomMargin = bottomMargin
                binding.root.layoutParams = params
            }
        }

        private fun dpToPx(dp: Float): Int {
            return (dp * binding.root.resources.displayMetrics.density).toInt()
        }
    }

    class LanguageDiffCallback: DiffUtil.ItemCallback<LanguageItem>(){
        override fun areItemsTheSame(
            oldItem: LanguageItem,
            newItem: LanguageItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LanguageItem,
            newItem: LanguageItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun backgroundFor(item: LanguageItem): Int {
        return when (item) {
            is LanguageItem.Child -> {
                when (item.position) {
                    ChildPosition.SINGLE -> R.drawable.bg_item_language_child_single_selector
                    ChildPosition.FIRST -> R.drawable.bg_item_language_child_top_selector
                    ChildPosition.MIDDLE -> R.drawable.bg_item_language_child_middle_selector
                    ChildPosition.LAST -> R.drawable.bg_item_language_child_bottom_selector
                }
            }
            else -> R.drawable.bg_item_language_parent_selector
        }
    }
}
