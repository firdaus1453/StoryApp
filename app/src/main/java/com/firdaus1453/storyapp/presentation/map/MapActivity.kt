package com.firdaus1453.storyapp.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.databinding.ActivityMapBinding
import com.firdaus1453.storyapp.presentation.ViewModelFactory
import com.firdaus1453.storyapp.util.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_map)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val mViewModel: MapViewModel by viewModels {
            factory
        }
        viewModel = mViewModel

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        with(viewModel) {
            observe(stories, ::storiesStateView)
        }

        binding.sfMap.setOnRefreshListener {
            viewModel.getStories()
        }
    }

    private fun storiesStateView(result: Result<List<Stories>?>) {
        when (result) {
            is Result.Success -> {
                if (result.data?.isNotEmpty() == true) {
                    for (x in result.data) {
                        if (x.lat != 0.0 && x.lon != 0.0) {
                            val latLng = x.lat?.let { x.lon?.let { it1 -> LatLng(it, it1) } }
                            val addressName = getAddressName(x.lat ?: 0.0, x.lon ?: 0.0)
                            latLng?.let {
                                MarkerOptions().position(it).title(x.name).snippet(addressName)
                            }
                                ?.let {
                                    mMap.addMarker(
                                        it
                                    )
                                }
                            if (latLng != null) {
                                boundsBuilder.include(latLng)
                            }
                        }
                    }

                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                binding.sfMap.isRefreshing = false
            }
            is Result.Error -> {
                binding.sfMap.isRefreshing = false
                Toast.makeText(this, getString(R.string.error_loading), Toast.LENGTH_LONG)
                    .show()
            }
            Result.Loading -> {
                binding.sfMap.isRefreshing = true
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getMyLocation()
        setMapStyle()
        viewModel.getStories()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MAP", "Failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MAP", "Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}