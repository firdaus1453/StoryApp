package com.firdaus1453.storyapp.presentation.createstory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.databinding.ActivityDetailBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.detail.DetailViewModel

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
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

        with(viewModel) {
//            observe(story, ::storyViewState)
        }
    }
}