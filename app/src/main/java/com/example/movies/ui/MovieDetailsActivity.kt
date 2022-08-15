package com.example.movies.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.movies.R
import com.squareup.picasso.Picasso

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var movieName :TextView
    private lateinit var movieDate :TextView
    private lateinit var movieDescription :TextView
    private lateinit var movieRate :TextView

    private lateinit var moviePoster :ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        // change color of action bar
        val actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#eb8f2d"))
        actionBar!!.setBackgroundDrawable(colorDrawable)

        initComponent()

        var intent = intent
        val name = intent.getStringExtra("movieName")
        val description = intent.getStringExtra("movieDesc")
        val date  = intent.getStringExtra("movieDate")
        val posterPath = intent.getStringExtra("moviePoster")
        val rate = intent.getStringExtra("movieRate")

        this.setTitle(name)

        movieName.text = name
        movieDate.text = date
        movieDescription.text = description
        movieRate.text = rate
        Picasso.get().load("https://image.tmdb.org/t/p/w500${posterPath}").resize(200,300).into(moviePoster)

    }
    fun initComponent(){

        movieName = findViewById(R.id.movie_name_details)
        movieDate = findViewById(R.id.movie_date_details)
        movieDescription = findViewById(R.id.description_details)
        movieRate = findViewById(R.id.rate)
        moviePoster = findViewById(R.id.movie_poster_details)

    }
}