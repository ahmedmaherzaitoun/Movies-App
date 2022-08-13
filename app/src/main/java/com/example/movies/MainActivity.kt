package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var genreList: ArrayList<GenreModel>
    private lateinit var genreRecyclerViewAdapter: GenreRecyclerViewAdapter
    private lateinit var genreRecyclerView: RecyclerView

    private lateinit var movieList: ArrayList<MovieModel>
    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()


        // get generes of movies
        var moviesInterface = MoviesApiClient.getInstance().create(MoviesInterface::class.java)
        // launching a new coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val result = moviesInterface.getGenre()
            genreList.add(GenreModel(-1,"All"))
            if (result != null) {
                // Checking the results
                val genresJsonArray = result.body()!!.genres
                for (genre in genresJsonArray) {
                    genreList.add(GenreModel( genre.asJsonObject.get("id") .asInt,genre.asJsonObject.get("name").toString().substring(1,genre.asJsonObject.get("name").toString().length-1)))
                }
                genreRecyclerViewAdapter.notifyDataSetChanged()
            }

        }
        GlobalScope.launch(Dispatchers.Main) {
            // get movies
            val response = moviesInterface.getMovies()
            if (response != null) {
                // Checking the results
                val jsonObj = response.body()

                val gson = Gson()
                val movieObj= gson.fromJson(jsonObj, MoviesJsonModel::class.java)

                for (movie in movieObj.results) {
                    val id = movie.asJsonObject.get("id").asInt
                    val name = movie.asJsonObject.get("title").toString().substring(1,movie.asJsonObject.get("title").toString().length-1)
                    val date = movie.asJsonObject.get("release_date").toString().substring(1,movie.asJsonObject.get("release_date").toString().length-1)
                    val description = movie.asJsonObject.get("overview").toString().substring(1,movie.asJsonObject.get("overview").toString().length-1)
                    val mainImg = movie.asJsonObject.get("backdrop_path").toString().substring(1,movie.asJsonObject.get("backdrop_path").toString().length-1)
                    val posterImg = movie.asJsonObject.get("poster_path").toString().substring(1,movie.asJsonObject.get("poster_path").toString().length-1)
                    val rate = movie.asJsonObject.get("vote_average").toString()

                    Log.d("zatona", name)
                    movieList.add(MovieModel( id= id,title= name,release_date=date,overview=description,backdrop_path=mainImg,poster_path=posterImg,vote_average=rate ))

                }
                movieRecyclerViewAdapter.notifyDataSetChanged()

            }
        }

        genreRecyclerViewAdapter = GenreRecyclerViewAdapter(genreList)
        genreRecyclerView.adapter = genreRecyclerViewAdapter

        movieRecyclerViewAdapter = MovieRecyclerViewAdapter(movieList,this)
        movieRecyclerView.adapter = movieRecyclerViewAdapter




    }

    private fun init(){
        genreRecyclerView = findViewById(R.id.genre_recyclerView)
        genreRecyclerView.setHasFixedSize(true)
        genreRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        genreList = ArrayList()

        movieRecyclerView = findViewById(R.id.movies_recyclerview)
        movieRecyclerView.setHasFixedSize(true)
       //movieRecyclerView.layoutManager = LinearLayoutManager(this)
        movieRecyclerView.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        movieList = ArrayList()



    }


}