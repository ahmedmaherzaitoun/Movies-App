package com.example.movies.ui.main


import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.ConnectivityReceiver
import com.example.movies.R
import com.example.movies.model.GenreModel
import com.example.movies.ui.MovieDetailsActivity
import com.example.movies.ui.SearchActivity
import com.example.movies.ui.adapter.GenreRecyclerViewAdapter
import com.example.movies.ui.adapter.MovieRecyclerViewAdapter
import com.example.movies.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), GenreRecyclerViewAdapter.OnItemClickListener,
    MovieRecyclerViewAdapter.OnItemClickListener,
    ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var genreList: ArrayList<GenreModel>
    private lateinit var genreRecyclerViewAdapter: GenreRecyclerViewAdapter
    private lateinit var genreRecyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var searchBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var connectionTV: TextView

    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var gridLayoutManager :GridLayoutManager

    private val viewModel: MainViewModel by viewModels()

    var page = 1
    var genreId = -1
    val totalPages = 500
    var isLoading = false

    var isConnected = false
    var initComponent = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // color actionBar
        val actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#eb8f2d"))
        actionBar!!.setBackgroundDrawable(colorDrawable)


        // connection of network
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        initComponent()

        // search by button and keyboard
        searchBtn.setOnClickListener(View.OnClickListener {
            search()
        })

        // search by press at keyboard
        searchET.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                return@OnEditorActionListener true
            }
            false
        })
        observeViewModel()
    }

    private fun observeViewModel(){
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
        Log.d("inlandscape", "observeViewModel: ")

        viewModel.movies.observe(this){
            Log.d("mvvm main movies", it.size.toString())
            movieRecyclerViewAdapter.differ.submitList(it.toList())
           //movieRecyclerView.adapter = movieRecyclerViewAdapter

        }
        viewModel.genreList.observe(this) {
            Log.d("mvvm genres", it.size.toString())
            genreRecyclerViewAdapter.setGenresList(it)
            //genreRecyclerView.adapter = genreRecyclerViewAdapter
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        //movies
        initScrollListener()
    }

    private fun initComponent(){
        initComponent = true
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
        Log.d("inlandscape", "initComponent: ")

        searchBtn = findViewById(R.id.search_btn_main)
        searchET = findViewById(R.id.search_et_main)

        genreRecyclerView = findViewById(R.id.genre_recyclerView)
        genreRecyclerView.setHasFixedSize(true)
        genreRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        genreList = ArrayList()

        movieRecyclerView = findViewById(R.id.movies_recyclerview)
        movieRecyclerView.setHasFixedSize(true)

        gridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        movieRecyclerView.layoutManager = gridLayoutManager

        //movie adapter
        movieRecyclerViewAdapter =
            MovieRecyclerViewAdapter(this, this, R.layout.movie_grid_layout)
        movieRecyclerView.adapter = movieRecyclerViewAdapter

        // genres adapter
        genreRecyclerViewAdapter = GenreRecyclerViewAdapter(this)
        genreRecyclerView.adapter = genreRecyclerViewAdapter

        progressBar = findViewById(R.id.main_progress_bar)
        connectionTV = findViewById(R.id.connection_tv)

    }
    private fun search(){

        val intent = Intent(this, SearchActivity::class.java)

        if(searchET.text.isEmpty()){
            Toast.makeText(this,"Search text is Empty",Toast.LENGTH_SHORT).show()
        }
        else if(!isConnected){
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show()
        }
        else {
            intent.putExtra("searchQuery", searchET.text.toString())
            startActivity(intent)
        }
    }

    override fun onGenreClick(id: Int) {

        if(isConnected) {
            genreId = id
            page = 1
            viewModel.moviesAfterHandling.clear()
            viewModel.getMovies(genreId,page)
        }else{
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onMovieClick(position: Int) {

        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movieName" ,movieRecyclerViewAdapter.differ.currentList[position].title)
        intent.putExtra("movieDesc" ,movieRecyclerViewAdapter.differ.currentList[position].overview )
        intent.putExtra("moviePoster" ,movieRecyclerViewAdapter.differ.currentList[position].poster_path )
        intent.putExtra("movieDate" ,movieRecyclerViewAdapter.differ.currentList[position].release_date )
        intent.putExtra("movieRate" ,movieRecyclerViewAdapter.differ.currentList[position].vote_average )

        startActivity(intent)
    }
    var isLastPage = false
    var isScrolling= false

    private fun initScrollListener(){

        movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //manually calculating payout numbers for pagination
                //check for scroll down
                if (dy > 0 ) {

                    if (isConnected) {
                    //bottom of list!
                        if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == movieRecyclerViewAdapter.itemCount-1 && page < totalPages) {
                            gridLayoutManager.isSmoothScrolling
                            page++
                            viewModel.getMovies(genreId,page)
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        this.isConnected = isConnected

        var sharedPref = getSharedPreferences("isConnected", MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.apply {
            putBoolean("isConnected",isConnected)
            apply()
        }
        Log.d("isConnected", "showNetworkMessage: $isConnected" )

        if (!isConnected ) {

            progressBar.visibility = View.INVISIBLE

            // movieList not empty
            if (viewModel.isLoading.value != null) {
                connectionTV.visibility = View.INVISIBLE
            }
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show()

        }else if (initComponent) {

            connectionTV.visibility = View.INVISIBLE
            viewModel.getGenres()

            if (viewModel.isLoading.value == null) {
                viewModel.getMovies(genreId,page)
            }else{
                progressBar.visibility = View.INVISIBLE
            }
            observeViewModel()
        }

    }





}