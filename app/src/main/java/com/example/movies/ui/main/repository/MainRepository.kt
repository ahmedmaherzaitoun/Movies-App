package com.example.movies.ui.main.repository

import com.example.movies.api.MoviesInterface
import javax.inject.Inject

class MainRepository @Inject constructor(private val moviesInterface: MoviesInterface) {


    suspend fun getMovies(genreID:String ,page:String) = moviesInterface.getMovies(genreID,page)
    suspend fun getGenres() = moviesInterface.getGenres()
    suspend fun getSearchMovies(query:String ,page:String) = moviesInterface.getSearchMovies(query,page)


}