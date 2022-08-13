package com.example.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GenreRecyclerViewAdapter (private val genreList: List<GenreModel>) :
    RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_grid_layout,parent,false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.genreName.text = genre.name
    }

    override fun getItemCount(): Int {
       return genreList.size
    }
    class GenreViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        val genreName : TextView = itemView.findViewById(R.id.genre_name)
    }
}