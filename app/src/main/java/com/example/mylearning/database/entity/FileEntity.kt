package com.example.mylearning.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mylearning.model.FileModel
import java.io.File

@Entity(
    tableName = "files",
    indices = [Index(value = ["path"], unique = true)]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val path: String,
    val name: String,
    val size: Long,
    val extension: String,
    val lastModified: Long,
    val fileType: String,
    val isDirectory: Boolean
)

//entity -> model
fun FileEntity.toModel(): FileModel{
    val file = File(this.path)
    return FileModel(
        file = file,
        name = name,
        path = this.path,
        size = size,
        extension = extension,
        lastModified = lastModified,
        isDirectory = this.isDirectory
    )
}