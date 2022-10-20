package com.firdaus1453.storyapp.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.databinding.FragmentHomeBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.util.observe

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: HomeViewModel by viewModels {
            factory
        }
        homeViewModel = viewModel
        with(homeViewModel) {
            observe(stories, ::storiesStateView)
            observe(token, ::tokenStateView)
        }
        binding.sfHome.setOnRefreshListener {
            homeViewModel.getToken()
            binding.sfHome.isRefreshing = false
        }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
    }

    private fun tokenStateView(token: String) {
        homeViewModel.getStories(token)
    }

    private fun storiesStateView(result: Result<List<Stories>?>) {
        when (result) {
            is Result.Success -> {
                Log.d("stories", "storiesStateView: ${result.data}")
                adapter = HomeAdapter(result.data ?: listOf())
                binding.rvStories.adapter = adapter
                binding.progressBarContainer.visibility = View.GONE
            }

            is Result.Error -> {
                Toast.makeText(requireContext(), "Gagal memuat, silahkan coba lagi", LENGTH_SHORT).show()
                binding.progressBarContainer.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBarContainer.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}