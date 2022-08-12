package com.example.movies

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesApiClient {
    val baseUrl = "https://api.themoviedb.org"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}