package com.example.movies

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesApiClient {
    val baseUrl = "https://api.themoviedb.org/3/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}