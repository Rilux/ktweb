package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
) : java.io.Serializable
