package com.firdaus1453.storyapp.presentation.createstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
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
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.FileUploadResponse
import com.firdaus1453.storyapp.databinding.ActivityCreateStoryBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.presentation.camera.CameraActivity
import com.firdaus1453.storyapp.presentation.camera.CameraActivity.Companion.CAMERA_X_RESULT
import com.firdaus1453.storyapp.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: CreateStoryViewModel by viewModels {
        factory
    }
    private var getFile: File? = null
    private var lat: Float? = null
    private var lon: Float? = null

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupView()
        getMyLastLocation()

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
                binding.progressBarContainer.visibility = View.GONE
                setResult(RESULT_OK)
                finish()
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

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(
                        this,
                        getString(R.string.message_permission_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
                val description = binding.edDescription.text.toString()
                viewModel.addNewStory(file, description, lat, lon)
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    @Suppress("DEPRECATION")
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
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}