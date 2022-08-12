package com.example.movies

import retrofit2.Response
import retrofit2.http.GET
interface MoviesInterface {
        @GET("/3/genre/movie/list?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getGenre() : Response<MoviesGenreModel>

}