package com.example.mylearning.model

data class Language2Item(
    val id: String,
    val name: String,
    val englishName: String,
    val flagRes: Int,
    var isSelected: Boolean = false
)