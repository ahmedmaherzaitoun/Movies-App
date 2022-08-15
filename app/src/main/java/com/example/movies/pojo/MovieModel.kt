package com.example.movies.pojo

data class MovieModel (
    val id:Int ,
    val title: String,
    val release_date:String,
    val overview:String,
    val backdrop_path: String,
    val poster_path:String,
    val vote_average:String,
)
