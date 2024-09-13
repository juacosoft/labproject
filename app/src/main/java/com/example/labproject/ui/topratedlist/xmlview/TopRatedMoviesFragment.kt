package com.example.labproject.ui.topratedlist.xmlview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.labproject.R
import com.example.labproject.databinding.FragmentTopratedmoviesBinding
import com.example.labproject.ui.topratedlist.TopRatedMoviesViewModel
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesState
import kotlinx.coroutines.launch

class TopRatedMoviesFragment: Fragment() {

    private lateinit var binding: FragmentTopratedmoviesBinding

    private lateinit var topRatedAdapter: TopRatedMovieAdapter

    private val viewModel: TopRatedMoviesViewModel by lazy {
        ViewModelProvider(this).get(TopRatedMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopratedmoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topRatedAdapter = TopRatedMovieAdapter { movieSelected ->
            Log.d("FragmenttopRated", "onViewCreated: $movieSelected")
            Toast.makeText(context, "movie: ${movieSelected.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvTopRatedMovies.apply {
            adapter = topRatedAdapter
            val gridLayoutManager = GridLayoutManager(context, 3)
            layoutManager = gridLayoutManager
        }
        binding.btnLoadMoreToprated.setOnClickListener {
            if(viewModel.cantLoadMore()) {
                viewModel.loadTopRatedMovies()
            }
        }

        viewModel.loadTopRatedMovies()
        observeflowData()
    }

    private fun observeflowData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moviesState.collect { state ->
                when(state) {
                    is TopRatedMoviesState.Error -> {
                        binding.loadMoreToprated.visibility = View.GONE
                        Log.d("FragmenttopRated", "observeflowData: ${state.errorType}")
                    }
                    TopRatedMoviesState.Loading -> {
                        binding.btnLoadMoreToprated.visibility = View.GONE
                        binding.loadMoreToprated.visibility = View.VISIBLE
                        Log.d("FragmenttopRated", "observeflowData: Loading")
                    }
                    is TopRatedMoviesState.Success -> {
                        binding.btnLoadMoreToprated.visibility = View.VISIBLE
                        val nextPage = state.data?.page?.plus(1)?: 0
                        binding.btnLoadMoreToprated.text = getString(R.string.load_page, nextPage.toString())
                        topRatedAdapter.addItems(state.data?.results ?: emptyList())
                        binding.loadMoreToprated.visibility = View.GONE
                        Log.d("FragmenttopRated", "observeflowData: ${state.data}")
                    }

                    is TopRatedMoviesState.LoadingMore -> {
                        binding.btnLoadMoreToprated.visibility = View.GONE
                        binding.loadMoreToprated.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        topRatedAdapter.clearList()
        viewModel.clearCache()
    }
}