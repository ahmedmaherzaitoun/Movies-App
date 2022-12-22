package com.example.movies.ui.main.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.movies.model.GenreModel
import com.example.movies.model.MovieModel
import com.example.movies.model.MoviesJsonModel
import com.example.movies.data.repository.MainRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.ArrayList
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor (private val repository: MainRepository
): ViewModel() {


    private val errorMessage = MutableLiveData<String>()
    private val movieList =MutableLiveData<List<MovieModel>>()
    private val moviesAfterHandling: ArrayList<MovieModel> = ArrayList()
    private val searchMoviesAfterHandling: ArrayList<MovieModel> = ArrayList()

    private val movieSearchList =MutableLiveData<List<MovieModel>>()

    private val genreList =MutableLiveData<List<GenreModel>>()
    private var geners : ArrayList<GenreModel> = ArrayList()
    private var page = 1
    private var isSearch = false
    private var job : Job? = null
    private val isLoading = MutableLiveData<Boolean>()

    init {
       // getMovies(-1,1)
       // getGenres()
    }


    @SuppressLint("SuspiciousIndentation")
    fun getMovies(genreID:Int, page:Int) {
        var genre = genreID.toString() ;
        if( genreID == -1) {
            genre = "="
        }
        this.page = page
        job = CoroutineScope(Dispatchers.Main).launch {
            // get movies
            Log.d("mvvmerror", "getMovies1")

            val response = repository.getMovies(genre, page.toString())
            Log.d("mvvmerror", "getMovies2")

            if (response.isSuccessful) {
                Log.d("mvvmerror", "getMovies3")

                val jsonObj = response.body()
                    if (jsonObj != null) {
                        movieList.postValue(handleEmptyValue(jsonObj))
                        isLoading.postValue(true)
                    }
                }else{
                    Log.d("mvvmerror", "getMovies: ${onError(response.message())}")
                    onCleared()
                }
        }
    }
    fun getSearchMovies(query:String ,page:String) {

        job = CoroutineScope(Dispatchers.IO).launch {
                // get movies
                val response = repository.getSearchMovies(query, page)
                if (response.isSuccessful) {
                    // Checking the results
                    val jsonObj = response.body()
                    if (jsonObj != null) {
                        isSearch = true
                        movieSearchList.postValue(handleEmptyValue(jsonObj))
                    }
                }
            }
    }
    private fun handleEmptyValue(jsonObj :JsonObject):ArrayList<MovieModel>{
        val gson = Gson()
        val movieObj = gson.fromJson(jsonObj, MoviesJsonModel::class.java)
        Log.d("mvvm movies handle" , "ana hena")

        for (movie in movieObj.results) {
            val id =
                if (movie.asJsonObject.get("id") == null) 1 else movie.asJsonObject.get(
                    "id"
                ).asInt
            val name =
                if (movie.asJsonObject.get("title") == null) "" else movie.asJsonObject.get(
                    "title"
                ).toString()
                    .substring(1, movie.asJsonObject.get("title").toString().length - 1)
            val date =
                if (movie.asJsonObject.get("release_date") == null) "" else movie.asJsonObject.get(
                    "release_date"
                ).toString().substring(
                    1,
                    movie.asJsonObject.get("release_date").toString().length - 1
                )
            val description =
                if (movie.asJsonObject.get("overview") == null) "" else movie.asJsonObject.get(
                    "overview"
                ).toString().substring(
                    1,
                    movie.asJsonObject.get("overview").toString().length - 1
                )
            val mainImg =
                if (movie.asJsonObject.get("backdrop_path") == null) "" else movie.asJsonObject.get(
                    "backdrop_path"
                ).toString().substring(
                    1,
                    movie.asJsonObject.get("backdrop_path").toString().length - 1
                )
            val posterImg =
                if (movie.asJsonObject.get("poster_path") == null) "" else movie.asJsonObject.get(
                    "poster_path"
                ).toString().substring(
                    1,
                    movie.asJsonObject.get("poster_path").toString().length - 1
                )
            val rate =
                if (movie.asJsonObject.get("vote_average") == null) "" else movie.asJsonObject.get("vote_average").toString()

            Log.d("mvvm", name + " " + page)
            if( isSearch){
                searchMoviesAfterHandling.add(
                    MovieModel(
                        id = id,
                        title = name,
                        release_date = date,
                        overview = description,
                        backdrop_path = mainImg,
                        poster_path = posterImg,
                        vote_average = rate
                    )
                )
            }else{
                moviesAfterHandling.add(
                    MovieModel(
                        id = id,
                        title = name,
                        release_date = date,
                        overview = description,
                        backdrop_path = mainImg,
                        poster_path = posterImg,
                        vote_average = rate
                    )
                )

            }
        }
        if(isSearch){
            isSearch = false
            return searchMoviesAfterHandling
        }
        return moviesAfterHandling
    }
    fun getGenres() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getGenres()
            geners.add(GenreModel(-1,"Recent"))
            if (result != null) {
                // Checking the results
                val genresJsonArray = result.body()!!.genres
                for (genre in genresJsonArray) {
                    geners.add(GenreModel( genre.asJsonObject.get("id") .asInt,genre.asJsonObject.get("name").toString().substring(1,genre.asJsonObject.get("name").toString().length-1)))
                }
                genreList.postValue(geners)
            }

        }
    }
    private fun onError(message: String) {
        errorMessage.value = message
        isLoading.value = false
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun observeMovieListLiveData():LiveData<List<MovieModel>>{
        return movieList
    }
    fun observeSearchMovieListLiveData():LiveData<List<MovieModel>>{
        return movieList
    }
    fun observeGenreListLiveData():LiveData<List<GenreModel>>{
        return genreList
    }
    fun getIsLoading():LiveData<Boolean>{
        return isLoading
    }
    fun getErrorMessage():LiveData<String>{
        return errorMessage
    }
    fun clearMoviesAfterHandling(){
        return moviesAfterHandling.clear()
    }


}