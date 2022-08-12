package com.example.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GenreRecyclerViewAdapter (private val genreList: List<String>) :
    RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreViewHolder>(){

    class GenreViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        val textView : TextView = itemView.findViewById(R.id.genre_name)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_grid_layout,parent,false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.textView.text = genre
    }

    override fun getItemCount(): Int {
       return genreList.size
    }
}