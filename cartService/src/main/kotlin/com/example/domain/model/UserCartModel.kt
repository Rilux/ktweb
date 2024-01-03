package com.example.domain.model

@kotlinx.serialization.Serializable
data class UserCartModel(
    val userId: Int,
    val items: List<Int>
) : java.io.Serializable


