package com.firdaus1453.storyapp.presentation.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.data.remote.response.LoginResult
import com.firdaus1453.storyapp.databinding.ActivityLoginBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.main.MainActivity
import com.firdaus1453.storyapp.presentation.signup.SignupActivity
import com.firdaus1453.storyapp.util.observe

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        val viewModel: LoginViewModel by viewModels {
            factory
        }
        loginViewModel = viewModel.apply {
            checkLogin()
        }
        with(loginViewModel) {
            observe(isLogin, ::isLoginState)
            observe(loginResult, ::onLoginViewState)
        }
    }

    private fun isLoginState(isLogin: Boolean) {
        if (isLogin) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun onLoginViewState(result: Result<LoginResult?>) {
        when (result) {
            is Result.Success -> {
                val resultData = result.data
                loginViewModel.saveUser(
                    UserModel(
                        resultData?.name ?: "",
                        resultData?.token ?: "",
                        true
                    )
                )
                binding.progressBarContainer.visibility = View.GONE
            }

            is Result.Error -> {
                showError()
                binding.progressBarContainer.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBarContainer.visibility = View.VISIBLE
            }
        }
    }


    private fun setupAction() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    binding.emailEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
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
                    loginViewModel.loginUser(LoginRequest(email, password))
                }
            }
        }

        binding.footerSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun showError() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_sory))
            setMessage(getString(R.string.failed_login))
            setPositiveButton(getString(R.string.try_again)) { _, _ ->
            }
            create()
            show()
        }
    }

}