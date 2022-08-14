package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(),MovieRecyclerViewAdapter.OnItemClickListener {
    private lateinit var movieList: ArrayList<MovieModel>
    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var moviesInterface :MoviesInterface
    private lateinit var gridLayoutManager : GridLayoutManager
    private lateinit var searchET: EditText
    private lateinit var searchBtn:Button

    var page = 1
    var query = ""
    val totalPages = 500
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        init()

        val intent = intent
        var searchQuery = intent.getStringExtra("searchQuery")
        searchET.setText(searchQuery)
        query = searchET.text.toString()

        searchBtn.setOnClickListener(View.OnClickListener {
            query = searchET.text.toString()
            getMovies()

        })

        // API
        moviesInterface = MoviesApiClient.getInstance().create(MoviesInterface::class.java)

        getMovies()

        movieRecyclerViewAdapter = MovieRecyclerViewAdapter(movieList,this,this,R.layout.movie_grid_layout)
        movieRecyclerView.adapter = movieRecyclerViewAdapter

        initScrollListener()


    }

    private fun init(){
        searchET = findViewById(R.id.search_et)
        searchBtn= findViewById(R.id.search_btn)

        movieRecyclerView = findViewById(R.id.movies_recyclerview_search)
        movieRecyclerView.setHasFixedSize(true)
        gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        movieRecyclerView.layoutManager = gridLayoutManager
        movieList = ArrayList()

    }
    override fun onMovieClick(position: Int) {

        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movieName" ,movieList[position].title)
        intent.putExtra("movieDesc" ,movieList[position].overview )
        intent.putExtra("moviePoster" ,movieList[position].poster_path )
        intent.putExtra("movieDate" ,movieList[position].release_date )

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
    fun getMovies(){

        GlobalScope.launch(Dispatchers.Main) {
            // get movies


            val response = moviesInterface.getSearchMovies(query,page.toString())
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