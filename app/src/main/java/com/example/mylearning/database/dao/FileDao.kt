package com.example.mylearning.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mylearning.database.entity.FileEntity

@Dao
interface FileDao {
    // sửa: chuyển Flow -> LiveData
    @Query("SELECT * FROM files")
    fun getAllFiles(): LiveData<List<FileEntity>>

    // sửa: chuyển Flow -> LiveData
    @Query("SELECT * FROM files WHERE fileType = :type")
    fun getFilesByType(type: String): LiveData<List<FileEntity>>

    // sửa: chuyển Flow -> LiveData
    @Query("SELECT * FROM files WHERE name LIKE '%' || :query || '%' ")
    fun searchFiles(query: String): LiveData<List<FileEntity>>

    // sửa: chuyển Flow -> LiveData
    @Query("SELECT * FROM files WHERE fileType = :type AND name LIKE '%' || :query || '%' ")
    fun searchFilesByType(type: String, query: String): LiveData<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("DELETE FROM files WHERE path= :path")
    suspend fun deleteFileByPath(path: String)

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun deleteFileById(id: Long)

    @Query("DELETE FROM files")
    suspend fun clearAllFile()

    @Query("SELECT COUNT(*) FROM files")
    suspend fun getFileCount(): Int
}