package com.firdaus1453.storyapp.presentation.startup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.databinding.ActivityStartupBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.login.LoginActivity
import com.firdaus1453.storyapp.presentation.main.MainActivity
import com.firdaus1453.storyapp.util.observe

class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: StartupViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        with(viewModel) {
            observe(userModel, ::checkLogin)
        }
    }

    private fun checkLogin(userModel: UserModel) {
        if (userModel.isLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}