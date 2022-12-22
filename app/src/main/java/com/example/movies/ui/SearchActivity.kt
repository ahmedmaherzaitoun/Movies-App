package com.example.movies.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import com.example.movies.R
import com.example.movies.databinding.ActivityMainBinding
import com.example.movies.databinding.ActivitySearchBinding
import com.example.movies.ui.adapter.MovieRecyclerViewAdapter
import com.example.movies.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), MovieRecyclerViewAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySearchBinding

    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var gridLayoutManager : GridLayoutManager
    private lateinit var sharedPreferences: SharedPreferences
    private var page = 1
    private var query = ""
    private val totalPages = 500
    private var isConnected = false

    ////
    private val  viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("isConnected", MODE_PRIVATE)

        val actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#eb8f2d"))
        actionBar!!.setBackgroundDrawable(colorDrawable)

        initComponent()

        // get search query from mainActivity
        val intent = intent
        var searchQuery = intent.getStringExtra("searchQuery")
        binding.searchEt.setText(searchQuery)
        query = binding.searchEt.text.toString()

        setSearchClick()
        observeViewModel()
        initScrollListener()

    }
    private fun observeViewModel() {
        viewModel.observeSearchMovieListLiveData().observe(this) {
            movieRecyclerViewAdapter.differ.submitList(it.toList())
        }
        viewModel.getErrorMessage().observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        if( sharedPreferences.getBoolean("isConnected", false) ) {
            if (viewModel.getSearchMoviesIsLoading().value == null) {
                viewModel.getSearchMovies(query,page)
            }
        }
    }
    private fun setSearchClick(){

        binding.searchBtn.setOnClickListener{
            if(binding.searchEt.text == null){
                Toast.makeText(this,"Search is Empty", Toast.LENGTH_SHORT)
            }
            else if(!sharedPreferences.getBoolean("isConnected", false)){
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }else {
                query = binding.searchEt.text.toString()
                search()
            }


        }
        binding.searchEt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                query = binding.searchEt.text.toString()
                search()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun initComponent(){
        binding.moviesRecyclerviewSearch.setHasFixedSize(true)
        gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        binding.moviesRecyclerviewSearch.layoutManager = gridLayoutManager

        movieRecyclerViewAdapter =
            MovieRecyclerViewAdapter(this, this, R.layout.movie_grid_layout)
        binding.moviesRecyclerviewSearch.adapter = movieRecyclerViewAdapter

    }
    private fun search(){
        if(binding.searchEt.text.isEmpty()){
            Toast.makeText(this,"Search text is Empty",Toast.LENGTH_SHORT).show()
        }else if(!sharedPreferences.getBoolean("isConnected", false)){
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show()
        }
        else {
            Log.d("mvvms search fun1", movieRecyclerViewAdapter.differ.currentList.size.toString())
            viewModel.clearSearchMoviesAfterHandling()
            viewModel.getSearchMovies(query,page)

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

    private fun initScrollListener(){
        if(sharedPreferences.getBoolean("isConnected", false)) {
            binding.moviesRecyclerviewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) { //check for scroll down
                        if ( sharedPreferences.getBoolean("isConnected", false)) {
                            Log.d("isConnected", "initScrollListener: $isConnected" )
                            if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() ==movieRecyclerViewAdapter.itemCount-1 && page < totalPages) {
                                page++
                                viewModel.getSearchMovies(query,page)
                                Log.d("zatonaPage", "done page$page")
                            }

                        }
                    }
                }
            })
        }
    }
}