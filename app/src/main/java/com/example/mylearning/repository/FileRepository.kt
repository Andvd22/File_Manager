package com.example.mylearning.repository

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.mylearning.database.dao.FileDao
import com.example.mylearning.database.entity.toModel
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.model.FilterAndSortParams
import com.example.mylearning.model.SortCriteria
import com.example.mylearning.model.SortOrder
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
    fun observeAllFilesBySortAndSearchAndFileType(params: FilterAndSortParams): LiveData<List<FileModel>> {
        val orderByColumn = when(params.sortCriteria){
            SortCriteria.DATE -> "lastmodified"
            SortCriteria.NAME -> "name"
            SortCriteria.SIZE -> "size"
        }

        val orderDirection = if(params.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"


        val sqlQuery = if(params.type== FileType.ALL) {"SELECT * FROM files " +
                "WHERE name LIKE '%${params.query}%' "+
                "ORDER BY $orderByColumn ${orderDirection}"} else {
            "SELECT * FROM files " +
                    "WHERE name LIKE '%${params.query}%' AND fileType = '${params.type}' "+
                    "ORDER BY $orderByColumn ${orderDirection}"
        }

        val simpleQuery = SimpleSQLiteQuery(sqlQuery)

        return fileDao.searchAndSortAndFileTypeFiles(simpleQuery).map {
            listEntity -> listEntity.map { entity -> entity.toModel() }
        }
    }


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

    suspend fun renameFile(file: FileModel, newName: String): FileModel = withContext(ioDispatcher){
        val oldFile = file.file
        if(!oldFile.exists()){
            throw IllegalStateException("File không tồn tại: ${file.path}")
        }

        val finalName = if(newName.contains(".oldFile.extension")){
            newName
        } else {
            "$newName.${oldFile.extension}"
        }

        val invalidChars = charArrayOf('<', '>', ':', '/', '\\', '|', '?', '*')
        if(finalName.any{invalidChars.contains(it) || it in invalidChars}){
            throw IllegalArgumentException("Tên file không hợp lệ: $finalName")
        }

        val parentDir = oldFile.parentFile
        val newFile = File(parentDir, finalName)

        if(newFile.exists() && newFile.absolutePath != oldFile.absolutePath){
            throw IllegalStateException("File đã tồn tại: ${newFile.absolutePath}")
        }

        val renamed = try {
            oldFile.renameTo(newFile)
        } catch (e: SecurityException){
            throw SecurityException("Không có quyền đổi tên file: ${e.message}")
        } catch (e: Exception){
            throw IllegalStateException("Không thể đổi tên file: ${e.message}")
        }

        if(!renamed){
            throw IllegalStateException("Không thể đổi tên file: ${file.path}")
        }
        newFile.setLastModified(System.currentTimeMillis())

        FileModel(newFile)
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