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
import com.example.mylearning.model.FilterAndSortParams
import com.example.mylearning.model.SortCriteria
import com.example.mylearning.model.SortOrder
import com.example.mylearning.repository.FileRepository
import kotlinx.coroutines.launch

class FileViewModel(application: Application): AndroidViewModel(application){

    private val repository: FileRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        FileRepository(db.fileDao())
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _filterParams = MutableLiveData(FilterAndSortParams())
    val filterParams: LiveData<FilterAndSortParams> = _filterParams

    private val _shouldScrollToTop = MutableLiveData<Boolean>(false)
    val shouldScrollToTop: LiveData<Boolean> = _shouldScrollToTop


    fun updateFilterParams(type: FileType? =null, query: String? =null, sortCriteria: SortCriteria? = null, sortOrder: SortOrder? = null, isSortMode: Boolean? = null){
        val currentFilterParams = _filterParams.value ?: FilterAndSortParams()
        val newFilterParams = currentFilterParams.copy(
            type = type ?: currentFilterParams.type,
            query = query ?: currentFilterParams.query,
            sortCriteria = sortCriteria ?: currentFilterParams.sortCriteria,
            sortOrder = sortOrder ?: currentFilterParams.sortOrder,
            isSortMode = isSortMode ?: currentFilterParams.isSortMode
        )

        if(newFilterParams != currentFilterParams) {
            _filterParams.value = newFilterParams
            _shouldScrollToTop.value = true
        }
    }

    fun getFileByTypeAndQuery() = _filterParams.switchMap { filterParams ->
            if(filterParams.isSortMode==true){
                repository.observeAllFilesBySortAndSearchAndFileType(filterParams)
            }else{
                when(filterParams.type){
                    FileType.ALL -> repository.observeAllFilesAndSearchFiles(filterParams.query)
                    else -> repository.getFileByTypeAndQuery(filterParams.type,filterParams.query)
                }
            }
    }

    fun onScrolledToTop() {
        _shouldScrollToTop.value = false
    }

    fun refreshFiles(): Unit{
        viewModelScope.launch{
            _isLoading.value = true
            try {
                repository.refreshFiles()
                _isLoading.value = false
            } catch (_: Exception) {
                _isLoading.value = false
            }
        }
    }
    fun deleteFile(file: FileModel) = viewModelScope.launch {
        repository.deleteFile(file)
    }

    suspend fun renameFile(file: FileModel, newName: String) = repository.renameFile(file, newName)
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
