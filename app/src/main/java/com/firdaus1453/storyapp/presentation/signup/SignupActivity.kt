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
import androidx.core.widget.addTextChangedListener
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import com.firdaus1453.storyapp.databinding.ActivitySignupBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.login.LoginActivity
import com.firdaus1453.storyapp.util.observe

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
        with(signupViewModel) {
            observe(signupResult, ::signupStateView)
        }
    }

    private fun signupStateView(result: Result<String?>) {
        when (result) {
            is Result.Success -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_yeah))
                    setMessage(getString(R.string.account_create_success))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
                binding.progressBarContainer.visibility = View.GONE
            }

            is Result.Error -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_sory))
                    setMessage(getString(R.string.failed_register))
                    setPositiveButton(getString(R.string.try_again)) { _, _ ->
                    }
                    create()
                    show()
                }
                binding.progressBarContainer.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBarContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAction() {
        binding.nameEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                binding.nameEditTextLayout.error = null
            }
        }
        binding.emailEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                binding.emailEditTextLayout.error = null
            }
        }
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.error_fill_name)
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.error_fill_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.error_fill_password)
                }
                password.length < 6 -> {
                    binding.passwordEditTextLayout.error = getString(R.string.error_min_6_char)
                }
                else -> {
                    signupViewModel.signUp(SignupRequest(name, email, password))
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