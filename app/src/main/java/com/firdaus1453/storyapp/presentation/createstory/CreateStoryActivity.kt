package com.firdaus1453.storyapp.presentation.createstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.FileUploadResponse
import com.firdaus1453.storyapp.databinding.ActivityCreateStoryBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.camera.CameraActivity
import com.firdaus1453.storyapp.presentation.camera.CameraActivity.Companion.CAMERA_X_RESULT
import com.firdaus1453.storyapp.presentation.main.MainActivity
import com.firdaus1453.storyapp.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: CreateStoryViewModel by viewModels {
        factory
    }
    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        setupView()

        with(viewModel) {
            observe(addNewStory, ::addNewStoryViewState)
        }
    }

    private fun addNewStoryViewState(result: Result<FileUploadResponse?>) {
        when (result) {
            is Result.Success -> {
                if (result.data?.error == false) {
                    Toast.makeText(
                        this, result.data.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                binding.progressBarContainer.visibility = View.GONE
            }

            is Result.Error -> {
                Toast.makeText(
                    this, "Gagal memuat, silahkan coba lagi",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBarContainer.visibility = View.GONE
            }

            Result.Loading -> {
                binding.progressBarContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_detail_story)

        binding.ivCamera.setOnClickListener { startCameraX() }
        binding.ivGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }

    private fun uploadImage() {
        when {
            getFile == null -> {
                Toast.makeText(
                    this,
                    "Silakan masukkan berkas gambar terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.edDescription.text?.isNotEmpty() == false -> {
                Toast.makeText(
                    this,
                    "Silakan isi ceritamu terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            else -> {
                binding.progressBarContainer.visibility = View.VISIBLE
                val file = reduceFileImage(getFile as File)

                val description =
                    binding.edDescription.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                viewModel.addNewStory(imageMultipart, description)
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            getFile = bitmapToFile(result, applicationContext)

            binding.ivImgPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.ivImgPreview.setImageURI(selectedImg)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}