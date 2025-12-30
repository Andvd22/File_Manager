package com.example.mylearning.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.databinding.ItemFeatureToolBinding
import com.example.mylearning.model.ToolItem

class FeatureRequestItemAdapter (private val onClick: (ToolItem) -> Unit
) : ListAdapter<ToolItem, FeatureRequestItemAdapter.FeatureItemViewHolder>(FeatureItemDiffCallback){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureItemViewHolder {
        val binding = ItemFeatureToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeatureItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FeatureItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class FeatureItemViewHolder(
        private val binding: ItemFeatureToolBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onClick(getItem(position))
                }
            }
        }
        fun bind(item: ToolItem){
            // 1. Lấy LayoutParams của item
            val params = itemView.layoutParams as ViewGroup.MarginLayoutParams

            // 2. Lấy vị trí để biết nó là cột trái hay cột phải
            val position = adapterPosition
            val isLeftColumn = position % 2 == 0

            // 3. Tính toán Margin (Ví dụ 15dp ngang -> chia đôi mỗi bên 7.5dp)
            val marginHorizontal = dpToPx(7.5f)
            val marginBottom = dpToPx(12f)

            // Set margin: Cách lề giữa 7.5dp, lề dưới 12dp
            params.setMargins(
                if (isLeftColumn) 0 else marginHorizontal, // Cột trái không margin trái, cột phải margin trái 7.5dp
                0,
                if (isLeftColumn) marginHorizontal else 0, // Cột trái margin phải 7.5dp
                marginBottom
            )
            itemView.layoutParams = params

            binding.root.isSelected = item.isSelected
            binding.apply {
                ivIcon.setImageResource(item.iconRes)
                tvTitle.setText(item.titleRes)
            }
        }
        private fun dpToPx(dp: Float): Int {
            return (dp * binding.root.resources.displayMetrics.density).toInt()
        }
    }


    companion object FeatureItemDiffCallback: DiffUtil.ItemCallback<ToolItem>(){
        override fun areItemsTheSame(
            oldItem: ToolItem,
            newItem: ToolItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ToolItem,
            newItem: ToolItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}