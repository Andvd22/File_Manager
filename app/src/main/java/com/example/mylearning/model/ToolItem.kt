package com.example.mylearning.model

data class ToolItem(
    val id: Int,
    val iconRes: Int,
    val titleRes: Int,
    val isSelected: Boolean = false
)