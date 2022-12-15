package com.example.movies.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.model.MovieModel
import com.squareup.picasso.Picasso

class MovieRecyclerViewAdapter( private val context : Context, private val listener : OnItemClickListener, private val layout:Int) :
RecyclerView.Adapter<MovieRecyclerViewAdapter.MoviesViewHolder>() {

    private val differCallback= object : DiffUtil.ItemCallback<MovieModel>(){
        override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem.backdrop_path == newItem.backdrop_path
        }

        override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem == newItem
        }
    }
    // tool that will take the two list and tell the differences
    val differ= AsyncListDiffer(this, differCallback)


   /* fun addToMovieList(movies: MutableList<MovieModel>) {
        this.movieList.addAll(movies)
        notifyDataSetChanged()
    }*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return MoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.movieName.text = movie.title
        holder.movieDate.text = movie.release_date
       // "//https://api.themoviedb.org/3/movie/${movie.id}/images?api_key=0aeda53ab78b646d28c457d6abdeac6e"
       // https://play-lh.googleusercontent.com/dy1OYo2YklKN2AKL2QzSnz6to2sLly9uXzBEdDHt7saTDI6ErPVoSqTNL4MwlCR1waxe
        Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.poster_path}").resize(Resources.getSystem().getDisplayMetrics().widthPixels/2,(Resources.getSystem().getDisplayMetrics().widthPixels*3/4).toInt()).into(holder.movieImage)

        holder.movieImage.setOnClickListener{
            listener.onMovieClick(position)
           //notifyDataSetChanged()


        }
    }

    override fun getItemCount(): Int {
       return   differ.currentList.size
    }

     inner class MoviesViewHolder( view: View):RecyclerView.ViewHolder(view),View.OnClickListener{
         val movieImage : ImageView = view.findViewById(R.id.movie_img_item)
         val movieName : TextView = itemView.findViewById(R.id.movie_name_item)
         val movieDate : TextView = itemView.findViewById(R.id.movie_date_item)

         init{
             view.setOnClickListener(this)
         }

         override fun onClick(v: View?) {
             val position = adapterPosition
             if( position != RecyclerView.NO_POSITION) {
                 listener.onMovieClick(position)

             }
         }

     }
    interface OnItemClickListener{
        fun onMovieClick(position: Int)

    }

}