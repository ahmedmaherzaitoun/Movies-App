package com.example.movies.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.movies.R
import com.example.movies.databinding.ActivityMovieDetailsBinding
import com.squareup.picasso.Picasso

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var name:String
    private lateinit var description:String
    private lateinit var date:String
    private lateinit var posterPath:String
    private lateinit var rate:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // change color of action bar
        val actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#eb8f2d"))
        actionBar!!.setBackgroundDrawable(colorDrawable)


        getMovieFromIntent()
        setMovieFromIntentIntoComponents()


    }
    private fun getMovieFromIntent() {
        var intent = intent
        name = intent.getStringExtra("movieName").toString()
        description = intent.getStringExtra("movieDesc").toString()
        date  = intent.getStringExtra("movieDate").toString()
        posterPath = intent.getStringExtra("moviePoster").toString()
        rate = intent.getStringExtra("movieRate").toString()
        this.title = name
    }
    private fun setMovieFromIntentIntoComponents(){
        binding.movieNameDetails
        binding.movieNameDetails.text = name
        binding.movieDateDetails.text = date
        binding.descriptionDetails.text = description
        binding.rate.text = rate
        Picasso.get().load("https://image.tmdb.org/t/p/w500${posterPath}").resize(200,300).into(binding.moviePosterDetails)
    }
}
