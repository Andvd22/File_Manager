package com.example.mylearning

import java.io.File

data class FileModel(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val size: Long = if (file.isDirectory) 0 else file.length(),
    val extension: String = file.extension.lowercase(),
    val lastModified: Long = file.lastModified(),
    val isDirectory: Boolean = file.isDirectory
){
    /**
     * Lấy icon resource ID dựa trên loại file
     */
    fun getIconResource(): Int{
        return when {
            isDirectory -> R.drawable.ic_launcher_foreground
            name.endsWith(".pdf") -> R.drawable.ic_launcher_foreground
            extension in listOf("doc","docx","txt") -> R.drawable.ic_launcher_foreground
            extension in listOf("jpg", "jpeg", "png", "gif", "webp") -> android.R.drawable.ic_menu_gallery
            extension in listOf("mp3", "wav", "m4a", "flac") -> android.R.drawable.ic_lock_silent_mode_off
            extension in listOf("mp4", "avi", "mkv", "mov") -> android.R.drawable.ic_menu_camera
            extension in listOf("zip", "rar", "7z") -> android.R.drawable.ic_menu_save
            else -> android.R.drawable.ic_menu_info_details
        }
    }

    /**
     * Format kích thước file thành string dễ đọc
     */
    fun getFormattedSize(): String {
        if(isDirectory) return "Folder"

        return when {
            size < 1024 -> "$size B"
            size < 1024* 1024 -> "${size/1024} KB"
            size < 1024 * 1024 * 1024 -> "${size/(1024*1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }

    /**
     * Format thời gian sửa đổi cuối cùng
     */
    fun getFormattedDate(): String {
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(lastModified))
    }
}

/**
 * Enum class đại diện cho các loại file filter
 */
enum class FileType{
    ALL,
    DOCUMENT,
    IMAGE,
    VIDEO,
    AUDIO,
    ARCHIVE;

    fun getExtensions(): List<String> {
        return when (this) {
            ALL -> emptyList()
            DOCUMENT -> listOf("pdf", "doc", "docx", "txt", "rtf", "odt")
            IMAGE -> listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
            VIDEO -> listOf("mp4", "avi", "mkv", "mov", "wmv", "flv")
            AUDIO -> listOf("mp3", "wav", "m4a", "flac", "aac", "ogg")
            ARCHIVE -> listOf("zip", "rar", "7z", "tar", "gz")
        }
    }
}