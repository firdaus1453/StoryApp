package com.firdaus1453.storyapp.presentation.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.databinding.ActivityLoginBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.main.MainActivity
import com.firdaus1453.storyapp.presentation.signup.SignupActivity

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
            checkLogin().observe(this@LoginActivity) {
                if (it) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
//        with(loginViewModel) {
//            observe(loginResponse, ::onLoginViewState)
//        }
    }

    /*private fun onLoginViewState(liveData: LiveData<Result<String?>>) {
        liveData.value.let {  result ->
            if (result != null) {
                when(result) {
                    is Result.Error -> {
                        AlertDialog.Builder(this).apply {
                            setTitle("Maff")
                            setMessage("Anda gagal login")
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
                            setMessage("Anda berhasil login.")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
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
    }*/


    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    loginViewModel.loginUser(LoginRequest(email, password))
                        .observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Error -> {
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Maaf")
                                            setMessage("Anda gagal login")
                                            setPositiveButton("coba lagi") { _, _ ->
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                    Result.Loading -> {

                                    }
                                    is Result.Success -> {
                                        val loginResult = result.data
                                        loginViewModel.saveUser(
                                            UserModel(
                                                loginResult?.name ?: "",
                                                loginResult?.token ?: "",
                                                true
                                            )
                                        )
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Yeah!")
                                            setMessage("Anda berhasil login.")
                                            setPositiveButton("Lanjut") { _, _ ->
                                                val intent =
                                                    Intent(context, MainActivity::class.java)
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

        binding.footerSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
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