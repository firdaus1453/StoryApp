package com.firdaus1453.storyapp.presentation.detail

import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firdaus1453.storyapp.databinding.ActivityDetailBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory


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

        storyId = intent.getStringExtra(KEY_ID_STORY) ?: ""
        binding.tvName.text = storyId

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val KEY_ID_STORY = "KEY_ID_STORY"
    }
}