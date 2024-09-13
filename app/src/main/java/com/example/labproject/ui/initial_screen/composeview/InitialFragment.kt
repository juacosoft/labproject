package com.example.labproject.ui.initial_screen.composeview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labproject.R

class InitialFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    InitialFragmentView()
                }
            }
        }
    }

    @Composable
    private fun InitialFragmentView() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                findNavController().navigate(InitialFragmentDirections.initialFragmentNavigateToTopRatedMoviesFragment())
            }) {
                Text(text = "Native XML")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                findNavController().navigate(R.id.topRatedMoviesOnlyCompose)
            }) {
                Text(text = "Native Compose")
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Cross Compose XML",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                findNavController().navigate(InitialFragmentDirections.initialFragmentNavigateToTopRatedMoviesFragmentCompose())
            }) {
                Text(text = "Case 1 LazyColumn and RecyclerView")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                findNavController().navigate(InitialFragmentDirections.initialFragmentNavigateToTopRatedMoviesComposeWithIVFragment())
            }) {
                Text(text = "Case 1 LazyColumn and xml itemCard")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun InitialFragmentPreview() {
        MaterialTheme {
            InitialFragmentView()
        }
    }
}
