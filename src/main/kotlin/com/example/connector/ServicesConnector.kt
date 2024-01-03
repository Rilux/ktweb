package com.example.connector

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/*
interface CatalogueService {
    @GET("/users/{id}")
    suspend fun get(@Path("id") id: Int) : User?

    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: Int) : User?
}

object CatalogueRetrofitHelper {
    private var retrofit = Retrofit.Builder()
        .baseUrl("http://0.0.0.0:8090/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()


    private var service = retrofit.create(UserService::class.java)

    fun getService() = service
}

interface CatalogueService {
    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: Int) : User?
}

object CatalogueRetrofitHelper {
    private var retrofit = Retrofit.Builder()
        .baseUrl("http://0.0.0.0:8090/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()


    private var service = retrofit.create(UserService::class.java)

    fun getService() = service
}*/
