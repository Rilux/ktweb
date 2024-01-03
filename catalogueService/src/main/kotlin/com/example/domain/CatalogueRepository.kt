package com.example.domain

import com.example.domain.model.CatalogueItem
import com.example.domain.model.CatalogueItemExposed
import com.example.domain.model.SortStyle

interface CatalogueRepository {
    suspend fun addItemToCatalogue(item: CatalogueItemExposed) : Int

    /**Pagination
     * @param[skip] how many elements should be skipped after sorting from the beginning,
     * @param[limit] how many elements should be returned(page size),
     * @param[skip] enum for sorting style, if null - no sorting
     * */
    suspend fun getAllItems(skip: Int? = null, limit: Int? = null, sort: SortStyle? = null): List<CatalogueItem>

    suspend fun deleteItem(id: Int)

}



