package com.example.movies.pojo

import com.google.gson.JsonArray

data class MoviesJsonModel (
    val page : Int,
    val results:JsonArray,
    val  total_pages : Int,
    val total_results : Int
)

