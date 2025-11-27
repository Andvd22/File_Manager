package com.example.mylearning.model

data class FilterAndSortParams(
    val type: FileType = FileType.ALL,
    val query: String = "",
    val sortCriteria: SortCriteria = SortCriteria.NAME,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val isSortMode: Boolean = false
)