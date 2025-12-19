package com.example.mylearning.repository

import com.example.mylearning.data.LanguageData
import com.example.mylearning.model.LanguageItem
//
//interface LanguageRepository {
//    fun getInitialList(): List<LanguageItem>
//    fun getChildrenOf(parentId: String): List<LanguageItem.Child>
//    fun getAllItems(): List<LanguageItem>
//}
//
//class LanguageRepositoryImpl(
//    private val dataSource: LanguageData = LanguageData
//) : LanguageRepository {
//    override fun getInitialList(): List<LanguageItem> = dataSource.getInitialList()
//    override fun getChildrenOf(parentId: String): List<LanguageItem.Child> = dataSource.getChildrenOf(parentId)
//    override fun getAllItems(): List<LanguageItem> = dataSource.getAllItems()
//}


interface LanguageRepository {
    fun getInitialList(): List<LanguageItem>
    fun getChildrenOf(parentId: String): List<LanguageItem.Child>
    fun getAllItems(): List<LanguageItem>
}

class LanguageRepositoryImpl(
    private val dataSource: LanguageData = LanguageData
): LanguageRepository{
    override fun getInitialList() = dataSource.getInitialList()
    override fun getChildrenOf(parentId: String): List<LanguageItem.Child> {
        return dataSource.getChildrenOf(parentId)
    }

    override fun getAllItems(): List<LanguageItem> {
        return dataSource.getAllItems()
    }

}
