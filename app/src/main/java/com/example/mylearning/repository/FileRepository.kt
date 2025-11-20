package com.example.mylearning.repository

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.mylearning.database.dao.FileDao
import com.example.mylearning.database.entity.toModel
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.model.toEntity
import com.example.mylearning.util.FileScanner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.collections.map

class FileRepository (
    private val fileDao: FileDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
){
    fun observeAllFiles(): LiveData<List<FileModel>> =
        fileDao.getAllFiles()
            .map { entityList->entityList.map { entity->entity.toModel() } }

    fun observeAllFilesAndSearchFiles(query: String): LiveData<List<FileModel>> =
        fileDao.searchFiles(query)
            .map { entityList->entityList.map { entity->entity.toModel() } }
    fun getFileByTypeAndQuery(fileType: FileType?, query: String): LiveData<List<FileModel>> =
            fileDao.searchFilesByType(fileType!!.name, query)
                .map { entityList -> entityList.map { entity -> entity.toModel() }
        }
    suspend fun refreshFiles() = withContext(ioDispatcher){
        val scannedFiles = FileScanner.scanFileWithCoroutine ()
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
}

//suspend fun clearCache() = withContext(ioDispatcher) {
//    fileDao.clearAllFile()
//}
//
//// sửa: LiveData by type
//fun observeFilesByType(fileType: FileType): LiveData<List<FileModel>> =
//    if(fileType == FileType.ALL){
//        observeAllFiles()
//    }else{
//        fileDao.getFilesByType(fileType.name)
//            .map { entityList -> entityList.map { entity -> entity.toModel() } }
//    }
//
//// sửa: LiveData cho search (nếu cần)
//fun searchFiles(query: String, fileType: FileType?): LiveData<List<FileModel>> =
//    when (fileType){
//        null, FileType.ALL -> fileDao.searchFiles(query)
//            .map{entityList -> entityList.map { entity -> entity.toModel() }}
//        else -> fileDao.searchFilesByType(fileType.name, query)
//            .map { entityList -> entityList.map { entity -> entity.toModel() } }
//    }