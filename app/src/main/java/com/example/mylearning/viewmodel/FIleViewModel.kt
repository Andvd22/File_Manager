package com.example.mylearning.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.repository.FileRepository
import kotlinx.coroutines.launch

class FileViewModel(application: Application): AndroidViewModel(application){

    // xóa: observeJob + StateFlow vì chuyển sang LiveData
    private val repository: FileRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        FileRepository(db.fileDao())
    }

    private var latestFiles: List<FileModel> = emptyList()

    // sửa: dùng MediatorLiveData để dễ dàng thay nguồn dữ liệu
    private val _files = MediatorLiveData<List<FileModel>>().apply { value = emptyList() }
    val files: LiveData<List<FileModel>> = _files

    // sửa: StateFlow -> LiveData
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentType = FileType.ALL
    private var currentQuery =""

    fun refresh(type: FileType = currentType){
        currentType = type

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshFiles(fileType = currentType)
            } catch (_: Exception) {
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