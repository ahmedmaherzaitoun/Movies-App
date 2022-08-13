package com.example.movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlin.coroutines.coroutineContext

class MovieRecyclerViewAdapter(private val movieList: List<MovieModel> , private val context : Context) :
RecyclerView.Adapter<MovieRecyclerViewAdapter.MoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_grid_layout,parent,false)
        return MoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = movieList[position]
        holder.movieName.text = movie.title
        holder.movieDate.text = movie.release_date
        "//https://api.themoviedb.org/3/movie/${movie.id}/images?api_key=0aeda53ab78b646d28c457d6abdeac6e"
       // https://play-lh.googleusercontent.com/dy1OYo2YklKN2AKL2QzSnz6to2sLly9uXzBEdDHt7saTDI6ErPVoSqTNL4MwlCR1waxe
        Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.poster_path}").resize(135,170).into(holder.movieImage)
    }

    override fun getItemCount(): Int {
       return  movieList.size
    }

     class MoviesViewHolder(var view: View):RecyclerView.ViewHolder(view){
         val movieImage : ImageView = view.findViewById(R.id.movie_img_item)
         val movieName : TextView = itemView.findViewById(R.id.movie_name_item)
         val movieDate : TextView = itemView.findViewById(R.id.movie_date_item)


     }

}