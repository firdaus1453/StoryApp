package com.firdaus1453.storyapp.presentation.signup

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import com.firdaus1453.storyapp.databinding.ActivitySignupBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
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

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: SignupViewModel by viewModels {
            factory
        }
        signupViewModel = viewModel
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan email"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    signupViewModel.signUp(SignupRequest(name, email, password))
                        .observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Error -> {
                                        val error = result.error
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Maaf")
                                            setMessage(error)
                                            setPositiveButton("coba lagi") { _, _ ->
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                    Result.Loading -> {

                                    }
                                    is Result.Success -> {
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Yeah!")
                                            setMessage("Akunnya sudah jadi.")
                                            setPositiveButton("Lanjut") { _, _ ->
                                                val intent = Intent(context, LoginActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                                finish()
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                }
                            }

                        }
                }
            }
        }

        binding.footerLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}