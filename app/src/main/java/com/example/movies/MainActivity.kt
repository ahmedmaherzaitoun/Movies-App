package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var genreList: ArrayList<String>
    private lateinit var genreRecyclerViewAdapter: GenreRecyclerViewAdapter
    private lateinit var genreRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        init()

        // get generes of movies
        var moviesInterface = MoviesApiClient.getInstance().create(MoviesInterface::class.java)
        // launching a new coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val result = moviesInterface.getGenre()
            if (result != null) {
                // Checking the results
                val genresJsonArray = result.body()!!.genres
                for (genre in genresJsonArray) {
                   // val genreId = genre.asJsonObject.get("id")
                    val genreName = genre.asJsonObject.get("name").toString().substring(1,genre.asJsonObject.get("name").toString().length-1)
                    genreList.add(genreName)
                    Log.d("zatona", genreName)
                }
                genreRecyclerViewAdapter.notifyDataSetChanged()

            }
            genreList.add("All")



        }
        genreRecyclerViewAdapter = GenreRecyclerViewAdapter(genreList)
        genreRecyclerView.adapter = genreRecyclerViewAdapter






    }
    private fun init(){
        genreRecyclerView = findViewById(R.id.genre_recyclerView)
        genreRecyclerView.setHasFixedSize(true)
        genreRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        genreList = ArrayList()

    }

    override fun onStart() {
        super.onStart()

    }

}