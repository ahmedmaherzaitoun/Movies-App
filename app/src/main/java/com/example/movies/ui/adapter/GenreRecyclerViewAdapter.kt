package com.example.movies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.pojo.GenreModel

class GenreRecyclerViewAdapter (private val genreList: List<GenreModel>,
                                private val listener : OnItemClickListener
) :
    RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_grid_layout,parent,false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.genreName.text = genre.name
        holder.genreName.setOnClickListener{
            listener.onGenreClick(genre.id)

        }


    }

    override fun getItemCount(): Int {
       return genreList.size
    }
    inner class GenreViewHolder( itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{

        val genreName : TextView = itemView.findViewById(R.id.genre_name)

        init{
            itemView.setOnClickListener(this)
        }

       override fun onClick(v: View?) {
          val position = adapterPosition
           if( position != RecyclerView.NO_POSITION) {
                listener.onGenreClick(position)

           }
        }
    }
    interface OnItemClickListener{
        fun onGenreClick(position: Int)

    }
}