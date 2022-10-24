package com.firdaus1453.storyapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.databinding.ActivityMainBinding
import com.firdaus1453.storyapp.presentation.createstory.CreateStoryActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavMain

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        setupView()
    }

    private fun setupView() {
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, CreateStoryActivity::class.java)
            resultCreate.launch(intent)
        }
    }

    private val resultCreate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("ok", "selesai tambah")
            }
        }
}