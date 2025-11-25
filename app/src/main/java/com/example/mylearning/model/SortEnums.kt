package com.example.mylearning.model

enum class SortCriteria {
    DATE, // Ngày tháng
    NAME, // Tên
    SIZE  // Kích thước
}

enum class SortOrder {
    ASCENDING,  // Tăng dần (A-Z, 0-9)
    DESCENDING  // Giảm dần (Z-A, 9-0)
}