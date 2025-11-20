package com.example.mylearning.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.repository.FileRepository
import kotlinx.coroutines.launch

class FileViewModel(application: Application): AndroidViewModel(application){

    private val repository: FileRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        FileRepository(db.fileDao())
    }
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private var _filterParams = MutableLiveData(Pair(FileType.ALL,""))

    fun updateFilterParams(type: FileType? =null, query: String? =null){
        var currentFilterParams = _filterParams.value ?: Pair(FileType.ALL,"")
        var newType = type ?: currentFilterParams.first
        var newQuery = query ?: currentFilterParams.second
        if(newType != currentFilterParams.first || newQuery != currentFilterParams.second) {
            _filterParams.value = Pair(newType, newQuery)
        }
    }

    fun getFileByTypeAndQuery() = _filterParams.switchMap { (type, query) ->
            repository.getFileByTypeAndQuery(type, query)
        }

    fun refreshFiles(): Unit{
        viewModelScope.launch{
            _isLoading.value = true
            try {
                repository.refreshFiles()
            } catch (_: Exception) {
                _isLoading.value = false
            }
        }
    }
    fun deleteFile(file: FileModel) = viewModelScope.launch {
        repository.deleteFile(file)
    }
}


//    private var latestFiles: List<FileModel> = emptyList()
//
//    // sửa: dùng MediatorLiveData để dễ dàng thay nguồn dữ liệu
//    private val _files = MediatorLiveData<List<FileModel>>().apply { value = emptyList() }
//    val files: LiveData<List<FileModel>> = _files
//
//    // sửa: StateFlow -> LiveData

//
//    private var currentType = FileType.ALL
//    private var currentQuery =""
//
//    fun refresh(type: FileType = currentType){
//        currentType = type
//
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                repository.refreshFiles(fileType = currentType)
//            } catch (_: Exception) {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun updateQuery(query: String){
//        currentQuery = query
//        _files.value = applyQueryFilter(latestFiles, currentQuery)
//    }
//
//    fun deleteFile(file: FileModel) = viewModelScope.launch {
//        repository.deleteFile(file)
//        refresh(currentType)
//    }
//
//    private fun applyQueryFilter(files: List<FileModel>, query: String): List<FileModel>{
//        return if (query.isBlank()) files
//        else files.filter { it.name.contains(query, ignoreCase = true) }
//    }
