package com.firdaus1453.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.databinding.FragmentHomeBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.detail.DetailActivity
import com.firdaus1453.storyapp.util.observe

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter

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
        with(viewModel) {
            observe(stories, ::storiesStateView)
        }
        binding.sfHome.setOnRefreshListener {
            viewModel.getStories()
            binding.sfHome.isRefreshing = false
        }
        adapter = HomeAdapter { iv, id ->
            onItemClicked(iv, id)
        }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
    }

    private fun storiesStateView(result: Result<List<Stories>?>) {
        when (result) {
            is Result.Success -> {
                adapter.submitList(result.data)
                binding.rvStories.adapter = adapter
                binding.progressBarContainer.visibility = View.GONE
            }

            is Result.Error -> {
                Toast.makeText(requireContext(), getString(R.string.fail_loading), LENGTH_SHORT)
                    .show()
                binding.progressBarContainer.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBarContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun onItemClicked(iv: ImageView, id: String) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.KEY_ID_STORY, id)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            iv, getString(R.string.shared_transition)
        )
        startActivity(intent, options.toBundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}