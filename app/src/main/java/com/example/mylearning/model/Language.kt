package com.example.mylearning.model

sealed class LanguageItem {
    abstract val id: String
    abstract val name: String
    abstract val isSelected: Boolean

    //system language
    data class System(
        override val id: String = "system",
        override val name: String = "System Language",
        override val isSelected: Boolean = false,
    ) : LanguageItem()

    data class Parent(
        override val id: String ,
        override val name: String,
        val flagRes: Int,
        val isExpanded: Boolean = false,
        val hasChildren: Boolean = false,
        override val isSelected: Boolean = false
    ) : LanguageItem()

    data class Child(
        override val id: String ,
        override val name: String,
        val flagRes: Int,
        val parentId: String,
        override val isSelected: Boolean = false,
        val position: ChildPosition = ChildPosition.SINGLE
    ) : LanguageItem()
}

enum class ChildPosition {
    SINGLE,
    FIRST,
    MIDDLE,
    LAST
}