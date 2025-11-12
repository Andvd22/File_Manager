package com.example.mylearning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.mylearning.databinding.ItemFileBinding

class FileAdapter (
    private val onItemClick: (FileModel) -> Unit,
    private val onMoreClick: (FileModel) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>(){

    private var files = listOf<FileModel>()
    fun submitList(newFiles: List<FileModel>){
        val diffCallback = FileDiffCallback(files, newFiles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        files = newFiles
        diffResult.dispatchUpdatesTo(this)
    }

    private class FileDiffCallback(
        private var oldList: List<FileModel>,
        private var newList: List<FileModel>
    ):DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return oldList[oldItemPosition].path == newList[newItemPosition].path
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
                    && oldList[oldItemPosition].size == newList[newItemPosition].size
                    && oldList[oldItemPosition].lastModified == newList[newItemPosition].lastModified
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
        holder.bind(files[position])
    }

    override fun getItemCount(): Int {
        return files.size
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