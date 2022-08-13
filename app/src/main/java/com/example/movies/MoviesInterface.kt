package com.example.movies

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface MoviesInterface {
        @GET("genre/movie/list?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getGenre() : Response<MoviesGenreModel>
        @GET("discover/movie?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getMovies() : Response<JsonObject>

        //suspend fun getMovies(@Url parms:String) : Response<MovieModel>
}