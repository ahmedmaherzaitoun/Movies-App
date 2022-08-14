package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), GenreRecyclerViewAdapter.OnItemClickListener ,MovieRecyclerViewAdapter.OnItemClickListener {
    private lateinit var genreList: ArrayList<GenreModel>
    private lateinit var genreRecyclerViewAdapter: GenreRecyclerViewAdapter
    private lateinit var genreRecyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var searchBtn: Button

    private lateinit var movieList: ArrayList<MovieModel>
    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var moviesInterface :MoviesInterface
    private lateinit var gridLayoutManager :GridLayoutManager

    var page = 1
    var genreId = -1
    val totalPages = 500
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        searchBtn.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, SearchActivity::class.java)
            if(searchET.text == null){
                Toast.makeText(this,"Search is Empty",Toast.LENGTH_SHORT)
            }else {
                intent.putExtra("searchQuery", searchET.text.toString())
                startActivity(intent)
            }
        })


        // API
        moviesInterface = MoviesApiClient.getInstance().create(MoviesInterface::class.java)

        // get generes of movies
        getGenres()

        genreRecyclerViewAdapter = GenreRecyclerViewAdapter(genreList,this)
        genreRecyclerView.adapter = genreRecyclerViewAdapter

        // get Movies
        getMovies()

        movieRecyclerViewAdapter = MovieRecyclerViewAdapter(movieList,this,this,R.layout.movie_grid_layout)
        movieRecyclerView.adapter = movieRecyclerViewAdapter

        initScrollListener()

    }

    private fun init(){

        searchBtn = findViewById(R.id.search_btn_main)
        searchET = findViewById(R.id.search_et_main)

        genreRecyclerView = findViewById(R.id.genre_recyclerView)
        genreRecyclerView.setHasFixedSize(true)
        genreRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        genreList = ArrayList()

        movieRecyclerView = findViewById(R.id.movies_recyclerview)
        movieRecyclerView.setHasFixedSize(true)
        gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        movieRecyclerView.layoutManager = gridLayoutManager
        movieList = ArrayList()

    }

    override fun onGenreClick(id: Int) {
        genreId = id
        page = 1
        movieList.clear()
        getMovies()
        movieRecyclerViewAdapter.notifyDataSetChanged()

    }

    override fun onMovieClick(position: Int) {

        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movieName" ,movieList[position].title)
        intent.putExtra("movieDesc" ,movieList[position].overview )
        intent.putExtra("moviePoster" ,movieList[position].poster_path )
        intent.putExtra("movieDate" ,movieList[position].release_date )
        intent.putExtra("movieRate" ,movieList[position].vote_average )

        startActivity(intent)
    }

    fun initScrollListener(){
        movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    if (!isLoading) {
                        //bottom of list!
                        if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == movieList.size - 1 && page < totalPages) {

                            page++
                            getMovies()
                            isLoading = true
                            Log.d("zatonaPage", "done page$page")
                        }
                    }
                }
            }
        })

    }

    fun getGenres() {
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
    }
    fun getMovies(){

        GlobalScope.launch(Dispatchers.Main) {
            // get movies
            var genre = genreId.toString() ;
            if( genreId == -1){
                genre = "="
            }

            val response = moviesInterface.getMovies(genre,page.toString())
            if (response != null) {
                // Checking the results
                val jsonObj = response.body()

                val gson = Gson()
                val movieObj= gson.fromJson(jsonObj, MoviesJsonModel::class.java)

                for (movie in movieObj.results) {
                    val id = if(movie.asJsonObject.get("id") ==null) 1 else movie.asJsonObject.get("id").asInt
                    val name =if(movie.asJsonObject.get("title") ==null) "" else movie.asJsonObject.get("title").toString().substring(1,movie.asJsonObject.get("title").toString().length-1)
                    val date =if(movie.asJsonObject.get("release_date") ==null) "" else movie.asJsonObject.get("release_date").toString().substring(1,movie.asJsonObject.get("release_date").toString().length-1)
                    val description =if(movie.asJsonObject.get("overview") ==null) "" else movie.asJsonObject.get("overview").toString().substring(1,movie.asJsonObject.get("overview").toString().length-1)
                    val mainImg =if(movie.asJsonObject.get("backdrop_path") ==null) "" else movie.asJsonObject.get("backdrop_path").toString().substring(1,movie.asJsonObject.get("backdrop_path").toString().length-1)
                    val posterImg =if(movie.asJsonObject.get("poster_path") ==null) "" else movie.asJsonObject.get("poster_path").toString().substring(1,movie.asJsonObject.get("poster_path").toString().length-1)
                    val rate = if(movie.asJsonObject.get("vote_average") ==null) "" else movie.asJsonObject.get("vote_average").toString()

                    Log.d("zatona", name)
                    movieList.add(MovieModel( id= id ,title= name,release_date=date,overview=description,backdrop_path=mainImg,poster_path=posterImg,vote_average=rate ))

                }
                movieRecyclerViewAdapter.notifyDataSetChanged()
                isLoading = false

            }
        }

    }


}