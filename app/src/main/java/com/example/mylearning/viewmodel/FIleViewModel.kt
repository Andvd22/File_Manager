package com.example.mylearning.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.repository.FileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FileViewModel(application: Application): AndroidViewModel(application){

    private var observeJob: Job? = null
    private val repository: FileRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        FileRepository(db.fileDao())
    }

    private var latestFiles: List<FileModel> = emptyList()

    private val _files = MutableStateFlow<List<FileModel>>(emptyList())
    val files: StateFlow<List<FileModel>> = _files

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentType = FileType.ALL
    private var currentQuery =""

    fun refresh(type: FileType = currentType){
        currentType = type
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshFiles(fileType = currentType)
                repository.observeFilesByType(currentType).collect { list ->
                    latestFiles = list
                    _files.value = applyQueryFilter(latestFiles, currentQuery)
                    _isLoading.value = false
                }
            }catch (e: Exception){
                _isLoading.value = false
            }
        }
    }

    fun updateQuery(query: String){
        currentQuery = query
        _files.value = applyQueryFilter(latestFiles, currentQuery)
    }

    fun deleteFile(file: FileModel) = viewModelScope.launch {
        repository.deleteFile(file)
        refresh(currentType)
    }

    private fun applyQueryFilter(files: List<FileModel>, query: String): List<FileModel>{
        return if (query.isBlank()) files
        else files.filter { it.name.contains(query, ignoreCase = true) }
    }


}