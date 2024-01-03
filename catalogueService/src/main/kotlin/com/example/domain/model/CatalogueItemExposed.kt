package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogueItemExposed(
    val title: String,
    val description: String,
    val imageUrl: String,
) : java.io.Serializable
