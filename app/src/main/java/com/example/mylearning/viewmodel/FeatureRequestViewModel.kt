package com.example.mylearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mylearning.R
import com.example.mylearning.model.ToolItem

class FeatureRequestViewModel : ViewModel(){
    private val _smartTools = MutableLiveData<List<ToolItem>>()
    val smartTools: LiveData<List<ToolItem>> = _smartTools

    private val _aiTools = MutableLiveData<List<ToolItem>>()
    val aiTools: LiveData<List<ToolItem>> = _aiTools

    private val _otherText = MutableLiveData("")
    val otherText : LiveData<String> = _otherText

    val isSubmitEnable = MediatorLiveData<Boolean>().apply { value = false }

    init {
        initTools()
        isSubmitEnable.addSource(_smartTools){ checkSubmit() }
        isSubmitEnable.addSource(_aiTools){ checkSubmit() }
        isSubmitEnable.addSource(_otherText){ checkSubmit() }
    }

    private fun initTools(){
        _smartTools.value = listOf(
            ToolItem(1, R.drawable.feature_request_edit_files, R.string.fr_edit_files),
            ToolItem(2, R.drawable.feature_request_merge_split, R.string.fr_merge_split),
            ToolItem(3, R.drawable.feature_request_convert_files, R.string.fr_convert_files),
            ToolItem(4, R.drawable.feature_request_compress_files, R.string.fr_compress_files),
            ToolItem(5, R.drawable.feature_request_view_setting, R.string.fr_view_setting)
        )

        _aiTools.value = listOf(
            ToolItem(6, R.drawable.feature_request_chat_with_document, R.string.fr_chat_with_document),
            ToolItem(7, R.drawable.feature_request_chat_ai_document_summary, R.string.fr_ai_document_summary),
            ToolItem(8, R.drawable.feature_request_translate_document, R.string.fr_translate_document)
        )
    }

    fun toggleTool(toolId: Int){
        _smartTools.value = _smartTools.value?.map {
            if(it.id == toolId){ it.copy(isSelected = !it.isSelected)}
            else {it}
        }
        _aiTools.value = _aiTools.value?.map {
            if(it.id == toolId){it.copy(isSelected = !it.isSelected)} else it
        }
    }

    private fun checkSubmit(){
        val hasTool = _smartTools.value?.any{ it.isSelected } == true || _aiTools.value?.any{ it.isSelected } == true
        val hasText = _otherText.value.isNullOrBlank().not()

        isSubmitEnable.value = hasTool || hasText
    }

    fun getSelectedToolTitles(): List<Int>{
        return(_smartTools.value.orEmpty() + _aiTools.value.orEmpty())
            .filter { toolItem -> toolItem.isSelected }
            .map { toolItem -> toolItem.titleRes }
    }

    fun setOtherText(text: String) {
        _otherText.value = text
    }
}
