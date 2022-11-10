package com.firdaus1453.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.databinding.FragmentHomeBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.detail.DetailActivity
import com.firdaus1453.storyapp.presentation.login.LoginActivity
import com.firdaus1453.storyapp.presentation.map.MapActivity
import com.firdaus1453.storyapp.util.observe

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter
    private var isNewData = false

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
        with(viewModel) {
            observe(stories, ::storiesStateView)
            observe(notLogin, ::navigateToLogin)
        }
        setupRecyclerView()
        setupPaging()
        binding.sfHome.setOnRefreshListener {
            viewModel.getStories()
            binding.sfHome.isRefreshing = false
        }
        binding.ivMap.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
    }

    private fun setupPaging() {
        adapter.addLoadStateListener { state ->
            stateLoading(state.refresh is LoadState.Loading)
            val errorState = state.source.append as? LoadState.Error
                ?: state.source.prepend as? LoadState.Error
                ?: state.append as? LoadState.Error
                ?: state.prepend as? LoadState.Error

            if (errorState != null) {
                Toast.makeText(requireContext(), errorState.error.message, Toast.LENGTH_LONG).show()
            }

            if (state.source.refresh is LoadState.NotLoading && state.append.endOfPaginationReached) {
                if (adapter.itemCount < 1) {
                    isEmptyData(true)
                } else {
                    isEmptyData(false)
                }
            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStories.smoothScrollToPosition(0)
                }
            }
        })
    }

    private fun navigateToLogin(isLogin: Boolean) {
        if (isLogin) {
            viewModel.getStories()
        } else {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
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

    private fun storiesStateView(data: PagingData<StoriesEntity>) {
        adapter.submitData(lifecycle, data)
    }

    private fun isEmptyData(isEmpty: Boolean) {
        binding.groupEmptyData.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.groupContent.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun stateLoading(isLoading: Boolean) {
        binding.progressBarContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.groupContent.visibility = if (isLoading) View.GONE else View.VISIBLE
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