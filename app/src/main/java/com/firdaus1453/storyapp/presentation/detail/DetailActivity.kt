package com.firdaus1453.storyapp.presentation.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.Story
import com.firdaus1453.storyapp.databinding.ActivityDetailBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.util.observe
import android.R as ResourceAndroid


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var storyId: String
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: DetailViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_detail_story)

        storyId = intent.getStringExtra(KEY_ID_STORY) ?: ""
        viewModel.getDetailStory(storyId)

        with(viewModel) {
            observe(story, ::storyViewState)
        }
    }

    private fun storyViewState(result: Result<Story?>) {
        when (result) {
            is Result.Success -> {
                if (result.data != null) {
                    setupView(result.data)
                }
                binding.progressBar.visibility = View.GONE
            }

            is Result.Error -> {
                Toast.makeText(
                    this, getString(R.string.error_loading),
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.progressBar.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun setupView(story: Story) {
        binding.apply {
            tvName.text = story.name
            tvFirstLetterName.text = story.name?.substring(0, 1)
            tvItemDesc.text = story.description
            Glide.with(ivImgPoster)
                .load(story.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                )
                .into(ivImgPoster)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == ResourceAndroid.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val KEY_ID_STORY = "KEY_ID_STORY"
    }
}