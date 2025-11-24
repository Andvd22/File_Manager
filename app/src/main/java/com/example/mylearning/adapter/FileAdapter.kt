package com.example.mylearning.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.databinding.ItemFileBinding
import com.example.mylearning.model.FileModel

class FileAdapter (
    private val onItemClick: (FileModel) -> Unit,
    private val onMoreClick: (FileModel) -> Unit
) : ListAdapter<FileModel, FileAdapter.FileViewHolder>(FileDiffCallback){

    val selectedFiles = HashSet<FileModel>()

    fun toggleSelection(file: FileModel){
        if(selectedFiles.contains(file)){
            selectedFiles.remove(file)
        }else{
            selectedFiles.add(file)
        }
        val index = currentList.indexOf(file)
        if(index != -1) notifyItemChanged(index)
    }

    fun clearSelection(): Unit {
//        selectedFiles.clear()
//        notifyDataSetChanged()
        val listTmpSelectedFiles = selectedFiles.toList()
        selectedFiles.clear()
        listTmpSelectedFiles.forEach { file ->
            val index = currentList.indexOf(file)
            if(index !=-1) notifyItemChanged(index)
        }
    }

    companion object FileDiffCallback : DiffUtil.ItemCallback<FileModel>(){
        override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel
        ): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel
        ): Boolean {
            return oldItem.name == newItem.name
                    &&oldItem.size == newItem.size
                    &&oldItem.lastModified == newItem.lastModified
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FileViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    inner class FileViewHolder(
        private val binding: ItemFileBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(fileModel: FileModel){
            binding.apply {
                ivFileIcon.setImageResource(fileModel.getIconResource())
                tvFileName.text = fileModel.name
                tvFileInfo.text = buildString {
                    append(fileModel.getFormattedSize())
                    append(" * ")
                    append(fileModel.getFormattedDate())
                }

                if(selectedFiles.contains(fileModel)){
                    root.setBackgroundColor(Color.GRAY)
                    tvFileName.setTypeface(null, Typeface.BOLD)
                } else{
                    root.setBackgroundColor(Color.WHITE)
                    tvFileName.setTypeface(null, Typeface.NORMAL)
                }

                root.setOnClickListener {
                    onItemClick(fileModel)
                }

                btnMore.setOnClickListener {
                    onMoreClick(fileModel)
                }
            }
        }
    }
}