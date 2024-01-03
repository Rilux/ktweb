package com.example.data

import com.example.domain.CatalogueRepository
import com.example.domain.model.CatalogueItem
import com.example.domain.model.CatalogueItemExposed
import com.example.domain.model.SortStyle
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

class CatalogueRepositoryCachedImpl(
    private val delegate: CatalogueRepository,
    storagePath: File
) : CatalogueRepository {
    private val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(storagePath))
        .withCache(
            "catalogueCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Int::class.javaObjectType,
                CatalogueItem::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        )
        .build(true)

    private val catalogueCached =
        cacheManager.getCache("catalogueCache", Int::class.javaObjectType, CatalogueItem::class.java)

    override suspend fun addItemToCatalogue(item: CatalogueItemExposed): Int = delegate.addItemToCatalogue(item)
        .also { id -> catalogueCached.put(id, CatalogueItem(id, item.title, item.description, item.imageUrl)) }

    override suspend fun getAllItems(skip: Int?, limit: Int?, sort: SortStyle?): List<CatalogueItem> {
        var result = delegate.getAllItems().also { list ->
            when (sort) {
                SortStyle.BY_NAME_ASC -> {
                    list.sortedBy { item ->
                        item.title
                    }
                }
                else -> {
                    list.sortedByDescending { item ->
                        item.title
                    }
                }
            }
        }
        result = try {
            result.subList(skip ?: 0, (skip ?: 0) + (limit ?: 50))
        } catch (e: Exception) {
            result
        }
        return result
    }



    override suspend fun deleteItem(id: Int) {
        catalogueCached.remove(id)
        return delegate.deleteItem(id)
    }
}