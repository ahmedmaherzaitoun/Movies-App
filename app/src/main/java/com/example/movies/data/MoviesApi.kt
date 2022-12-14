package com.example.movies.data

import com.example.movies.model.MoviesGenreModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface MoviesApi {
        @GET("genre/movie/list?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getGenres() : Response<MoviesGenreModel>
        @GET("discover/movie?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getMovies(@Query("with_genres") genre_id:String,@Query("page") page:String) : Response<JsonObject>
        @GET("search/movie?api_key=0aeda53ab78b646d28c457d6abdeac6e")
        suspend fun getSearchMovies(@Query("query") query:String,@Query("page") page:String) : Response<JsonObject>

}