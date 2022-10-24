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
import com.firdaus1453.storyapp.presentation.login.LoginActivity
import com.firdaus1453.storyapp.util.observe


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
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
        val mViewModel: HomeViewModel by viewModels {
            factory
        }
        viewModel = mViewModel
        setupRecyclerView()
        with(viewModel) {
            observe(stories, ::storiesStateView)
            observe(notLogin, ::navigateToLogin)
        }
    }

    private fun navigateToLogin(token: String) {
        if (token.isEmpty()) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        } else {
            viewModel.getStories(token)
            binding.sfHome.setOnRefreshListener {
                viewModel.getStories(token)
                binding.sfHome.isRefreshing = false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter { iv, id ->
            onItemClicked(iv, id)
        }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.adapter = adapter
    }

    private fun storiesStateView(result: Result<List<Stories>?>) {
        when (result) {
            is Result.Success -> {
                if (result.data?.isNotEmpty() == true) {
                    stateDataIsNotEmpty()
                    setupRecyclerView()
                    adapter.submitList(result.data)
                } else {
                    stateDataIsEmpty()
                }
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

    private fun stateDataIsEmpty() {
        binding.apply {
            groupContent.visibility = View.GONE
            groupEmptyData.visibility = View.VISIBLE
        }
    }

    private fun stateDataIsNotEmpty() {
        binding.apply {
            groupContent.visibility = View.VISIBLE
            groupEmptyData.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        viewModel.getToken()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}