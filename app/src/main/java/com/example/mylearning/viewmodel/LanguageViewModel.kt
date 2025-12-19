package com.example.mylearning.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mylearning.data.LanguagePrefs
import com.example.mylearning.repository.LanguageRepository
import com.example.mylearning.repository.LanguageRepositoryImpl
import com.example.mylearning.model.ChildPosition
import com.example.mylearning.model.LanguageItem

class LanguageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: LanguageRepository = LanguageRepositoryImpl()

    private val _languages = MutableLiveData<List<LanguageItem>>(emptyList())
    val languages: LiveData<List<LanguageItem>> = _languages

    private val _selectedLanguageId = MutableLiveData(
        LanguagePrefs.getSelectedLanguage(application)
    )
    val selectedLanguageId: LiveData<String> = _selectedLanguageId

    private var searchQuery: String = ""
    private val expandedParents = mutableSetOf<String>()
    private var currentSelection = _selectedLanguageId.value ?: LanguagePrefs.getSelectedLanguage(application)

    init {
        refreshLanguages()
    }

    fun onSearchQueryChanged(query: String) {
        val trimmed = query.trim()
        if (searchQuery == trimmed) return
        searchQuery = trimmed
        refreshLanguages()
    }

    fun onLanguageClicked(item: LanguageItem) {
        when (item) {
            is LanguageItem.System -> selectLanguage(item.id)
            is LanguageItem.Parent -> {
                if (item.hasChildren) {
                    toggleParent(item.id)
                } else {
                    selectLanguage(item.id)
                }
            }
            is LanguageItem.Child -> selectLanguage(item.id)
        }
    }

    private fun toggleParent(parentId: String) {
        if (expandedParents.contains(parentId)) {
            expandedParents.remove(parentId)
        } else {
            expandedParents.add(parentId)
        }
        refreshLanguages()
    }

    private fun selectLanguage(languageId: String) {
        if (currentSelection == languageId) return
        currentSelection = languageId
        _selectedLanguageId.value = languageId
        LanguagePrefs.saveSelectedLanguage(getApplication(), languageId)
        refreshLanguages()
    }

    private fun refreshLanguages() {
        val list = if (searchQuery.isBlank()) {
            buildExpandedList()
        } else {
            buildFilteredList()
        }
        _languages.value = list
    }

    private fun buildExpandedList(): List<LanguageItem> {
        val items = repository.getInitialList()
        val result = mutableListOf<LanguageItem>()

        items.forEach { item ->
            when (item) {
                is LanguageItem.System -> result.add(
                    item.copy(isSelected = item.id == currentSelection)
                )
                is LanguageItem.Parent -> {
                    val isExpanded = expandedParents.contains(item.id)
                    result.add(
                        item.copy(
                            isExpanded = isExpanded,
                            isSelected = item.id == currentSelection
                        )
                    )

                    if (isExpanded && item.hasChildren) {
                        val children = repository.getChildrenOf(item.id)
                        val childCount = children.size
                        children.forEachIndexed { index, child ->
                            val position = when {
                                childCount == 1 -> ChildPosition.SINGLE
                                index == 0 -> ChildPosition.FIRST
                                index == childCount - 1 -> ChildPosition.LAST
                                else -> ChildPosition.MIDDLE
                            }
                            result.add(
                                child.copy(
                                    isSelected = child.id == currentSelection,
                                    position = position
                                )
                            )
                        }
                    }
                }
                is LanguageItem.Child -> {
                    result.add(
                        item.copy(isSelected = item.id == currentSelection)
                    )
                }
            }
        }

        return result
    }

    private fun buildFilteredList(): List<LanguageItem> {
        val query = searchQuery
        val matches = repository.getInitialList().filter {
            it.name.contains(query, ignoreCase = true)
        }
        val result = mutableListOf<LanguageItem>()
        matches.forEach { item ->
            if (item is LanguageItem.System) {
                result.add(item.copy(isSelected = item.id == currentSelection))
                return@forEach
            }

            if (item is LanguageItem.Parent) {
                val isExpanded = expandedParents.contains(item.id)
                result.add(
                    item.copy(
                        isExpanded = isExpanded,
                        isSelected = item.id == currentSelection
                    )
                )

                if (isExpanded && item.hasChildren) {
                    val children = repository.getChildrenOf(item.id)
                    val childCount = children.size
                    children.forEachIndexed { index, child ->
                        val position = when {
                            childCount == 1 -> ChildPosition.SINGLE
                            index == 0 -> ChildPosition.FIRST
                            index == childCount - 1 -> ChildPosition.LAST
                            else -> ChildPosition.MIDDLE
                        }
                        result.add(
                            child.copy(
                                isSelected = child.id == currentSelection,
                                position = position
                            )
                        )
                    }
                }
            }
        }
        return result
    }
}
