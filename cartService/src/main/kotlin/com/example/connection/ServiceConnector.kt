package com.example.connection

import com.example.domain.model.CatalogueModel
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogueService {
    @GET("/catalogue/{id}")
    suspend fun getCatalogueItem(@Path("id") id: Int) : CatalogueModel?
}

object RetrofitHelper {
    private var retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()


    private var service = retrofit.create(CatalogueService::class.java)

    fun getService() = service
}