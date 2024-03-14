package com.example.data

import com.example.domain.CartRepository
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

class CartRepositoryCachedImpl(
    private val delegate: CartRepository,
    storagePath: File
) : CartRepository {
    private val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(storagePath))
        .withCache(
            "cartCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Int::class.javaObjectType,
                IntArray::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        )
        .build(true)

    private val cartCached = cacheManager.getCache("cartCache", Int::class.javaObjectType, IntArray::class.java)

    override suspend fun addItemToCart(newUserId: Int, newItemId: Int) =
        delegate.addItemToCart(newUserId, newItemId)
            .also { id ->
                val item = cartCached.find { it.key == newUserId }
                if (item == null) {
                    val items = cartCached.put(newUserId, listOf(newItemId).toIntArray())
                } else {
                    val items = cartCached.get(newUserId).toMutableList()
                    items.add(newItemId)
                    cartCached.put(newUserId, items.toIntArray())
                }
            }

    override suspend fun getAllForUser(userId: Int): List<Int> {
        val item = cartCached.find { it.key == userId }
        return if (item == null) {
            delegate.getAllForUser(userId)
                .also { list -> cartCached.put(userId, list.toIntArray()) }
        } else {
            cartCached[userId].asList().ifEmpty {
                delegate.getAllForUser(userId)
                    .also { list -> cartCached.put(userId, list.toIntArray()) }
            }
        }
    }


    override suspend fun clearCartForUser(userId: Int) {
        cartCached.remove(userId)
        return delegate.clearCartForUser(userId)
    }

    override suspend fun deleteItemFromCart(userId: Int, itemId: Int) {
        val items = cartCached.get(userId).toMutableList()
        items.remove(itemId)
        cartCached.put(userId, items.toIntArray())
        return delegate.deleteItemFromCart(userId, itemId)
    }
}