package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var movieName :TextView
    private lateinit var movieDate :TextView
    private lateinit var movieDescription :TextView
    private lateinit var moviePoster :ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        init()

        var intent = intent

        val name = intent.getStringExtra("movieName")
        val  description = intent.getStringExtra("movieDesc")
        val  date  = intent.getStringExtra("movieDate")
        val  posterPath = intent.getStringExtra("moviePoster")
        this.setTitle(name)


        movieName.text = name
        movieDate.text = date
        movieDescription.text = description

        moviePoster = findViewById(R.id.movie_poster_details)

        Picasso.get().load("https://image.tmdb.org/t/p/w500${posterPath}").resize(200,300).into(moviePoster)

    }
    fun init(){
        movieName = findViewById(R.id.movie_name_details)
        movieDate = findViewById(R.id.movie_date_details)
        movieDescription = findViewById(R.id.description_details)

        moviePoster = findViewById(R.id.movie_poster_details)


    }
}