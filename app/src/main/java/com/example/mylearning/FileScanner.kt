package com.example.mylearning

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.log

class FileScanner {
    companion object {
        suspend fun scanFileWithCoroutine(
            directory: File = Environment.getExternalStorageDirectory(),
            fileType: FileType = FileType.ALL,
            onProgress: (FileModel) -> Unit = {}
        ): List<FileModel> = withContext(Dispatchers.IO){
            Log.d(TAG, "🚀 [COROUTINE] Bắt đầu quét file với Coroutine...")
            val startTime = System.currentTimeMillis()
            try {
                val files = mutableListOf<FileModel>()
                scanDirectoryRecursive(directory, fileType, files, onProgress)
                val duration = System.currentTimeMillis() - startTime
                Log.d(TAG, "🚀 [COROUTINE] Hoàn thành! Tìm thấy ${files.size} files trong ${duration}ms")
                files
            }catch(e: Exception){
                Log.e(TAG, "🚀 [COROUTINE] Lỗi: ${e.message}", e)
                throw e
            }
        }

        suspend fun scanMutipleFoldersParallel(
            directories: List<File>,
            fileType: FileType = FileType.ALL
        ): List<FileModel> = withContext(Dispatchers.IO){
            Log.d(TAG, "⚡ [PARALLEL] Bắt đầu quét ${directories.size} folders song song...")
            val startTime = System.currentTimeMillis()

            val deferredResult = directories.map { directory ->
                async {
                    val files = mutableListOf<FileModel>()
                    scanDirectoryRecursive(directory, fileType, files){}
                    files
                }
            }

            val allFiles = deferredResult.flatMap { it.await() }

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "⚡ [PARALLEL] Hoàn thành! Tìm thấy ${allFiles.size} files trong ${duration}ms")
            allFiles
        }

        private fun scanDirectoryRecursive(
            directory: File,
            fileType: FileType,
            resultList: MutableList<FileModel>,
            onProgress: (FileModel) -> Unit
        ) {
            if (!directory.exists() || !directory.canRead()) {
                return
            }

            try {
                directory.listFiles()?.forEach { file ->
                    try {
                        if (file.isDirectory) {
                            scanDirectoryRecursive(file, fileType, resultList, onProgress)
                        } else {
                            if(matchesFileType(file, fileType)){
                                val fileModel = FileModel(file)
                                resultList.add(fileModel)
                                onProgress(fileModel)
                            }
                        }
                    } catch (e: SecurityException) {
                        Log.w(TAG, "Ko the doc: ${file.path}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi quét directory ${directory.path}: ${e.message}")
            }
        }

        private fun matchesFileType(file: File, fileType: FileType): Boolean{
            if(fileType == FileType.ALL) return true

            val extensions = fileType.getExtensions()
            return extensions.any { ext ->
                file.extension.equals(ext, ignoreCase = true)
            }
        }

        fun getCommonDirectories(): List<File> {
            val externalStorage = Environment.getExternalStorageDirectory()

            return listOfNotNull(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                externalStorage
            ).filter { it.exists() && it.canRead() }
        }

        suspend fun quickScan(directory: File): List<FileModel> =
            withContext(Dispatchers.IO){
                val files = mutableListOf<FileModel>()
                directory.listFiles()?.forEach { file ->
                    try {
                        files.add(FileModel(file))
                    }catch (e: Exception){

                    }
                }
                files.sortedByDescending { it.lastModified }
            }

    }
}