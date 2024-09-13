package com.example.labproject.ui.topratedlist.crosscomposeview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labproject.R
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.topratedlist.xmlview.TopRatedMovieAdapter

class TopRatedMoviesRecycler @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var topRatedMovieAdapter: TopRatedMovieAdapter

    init {
        View.inflate(context, R.layout.items_recycler, this)
    }

    fun setData(
        items: List<MovieEntity>,
        onMovieSelected: (MovieEntity) -> Unit
    ){
        val recyclerView = findViewById<RecyclerView>(R.id.items_recycler_top_rated_movies)

        if (!::topRatedMovieAdapter.isInitialized) {
            topRatedMovieAdapter = TopRatedMovieAdapter(onMovieSelected)
            recyclerView.adapter = topRatedMovieAdapter
            recyclerView.layoutManager = GridLayoutManager(context, 3)
        }
        topRatedMovieAdapter.clearList()
        topRatedMovieAdapter.addItems(items)
    }
}