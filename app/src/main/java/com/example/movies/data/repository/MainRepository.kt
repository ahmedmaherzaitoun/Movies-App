package com.example.movies.data.repository

import com.example.movies.data.MoviesApi
import javax.inject.Inject

class MainRepository @Inject constructor(private val moviesApi: MoviesApi) {


    suspend fun getMovies(genreID:String ,page:String) = moviesApi.getMovies(genreID,page)
    suspend fun getGenres() = moviesApi.getGenres()
    suspend fun getSearchMovies(query:String ,page:String) = moviesApi.getSearchMovies(query,page)


}