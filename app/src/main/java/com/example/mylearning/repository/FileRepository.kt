package com.example.mylearning.repository

import android.os.Environment
import com.example.mylearning.database.dao.FileDao
import com.example.mylearning.database.entity.toModel
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.model.toEntity
import com.example.mylearning.util.FileScanner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class FileRepository (
    private val fileDao: FileDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
){
    fun observeAllFiles(): Flow<List<FileModel>> =
        fileDao.getAllFiles()
            .map { entityList->entityList.map { entity->entity.toModel() } }

    fun observeFilesByType(fileType: FileType): Flow<List<FileModel>> =
        if(fileType == FileType.ALL){
            observeAllFiles()
        }else{
            fileDao.getFilesByType(fileType.name)
                .map { entityList -> entityList.map { entity -> entity.toModel() } }
        }

    fun searchFiles(query: String, fileType: FileType?): Flow<List<FileModel>> =
        when (fileType){
            null, FileType.ALL -> fileDao.searchFiles(query)
                .map{entityList -> entityList.map { entity -> entity.toModel() }}
            else -> fileDao.searchFilesByType(fileType.name, query)
                .map { entityList -> entityList.map { entity -> entity.toModel() } }
        }

    suspend fun refreshFiles(
        directory: File = Environment.getExternalStorageDirectory(),
        fileType: FileType = FileType.ALL
    ) = withContext(ioDispatcher){
        val scannedFiles = FileScanner.scanFileWithCoroutine (
            directory = directory,
            fileType = fileType
        )
        val entities = scannedFiles.map {model -> model.toEntity() }

        fileDao.clearAllFile()
        fileDao.insertFiles(entities)
    }

    suspend fun deleteFile(file: FileModel) = withContext(ioDispatcher) {
        val deleted = try {
            if (file.file.exists()) file.file.delete() else true
        } catch (exception: SecurityException) {
            false
        }

        if (deleted) {
            fileDao.deleteFileByPath(file.path)
        } else {
            throw IllegalStateException("Không thể xóa file vật lý: ${file.path}")
        }
    }

    suspend fun clearCache() = withContext(ioDispatcher) {
        fileDao.clearAllFile()
    }
}