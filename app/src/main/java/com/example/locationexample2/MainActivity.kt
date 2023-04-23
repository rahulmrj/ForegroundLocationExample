package com.example.locationexample2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.locationexample2.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
     var locationRequest: LocationRequest? = null
    private val permissonId = 307
    lateinit var myLocationCallback : LocationCallback
    lateinit var geocoder: Geocoder
    private var _mainBinding : ActivityMainBinding? = null
    val mainBinding get() = _mainBinding
    lateinit var viewModel : MainViewModel

   // private val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // ...

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.locationLiveData.observe(this, androidx.lifecycle.Observer {
            updateUI(it)
        })

        mainBinding!!.findLocationBtn.setOnClickListener {

            if (isLocatiponPermissionGranted().first) {
                if (isLocationEnable()) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            geocoder = Geocoder(this, Locale.getDefault())
                            val address = geocoder.getFromLocation(it.latitude, it.longitude, 2)
                            Log.e("#######", "onCreate: $address")
                            // viewModel.setViewModelLocation(address!!)
                            viewModel.setViewModelLocation(address!!)

                        } else {
                            locationRequest = createLocationRequest()

                            startLocationAlert()
                        }

                    }
                } else {
                    Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestLocationPermisson();
            }
        }




    }

    override fun onResume() {
        super.onResume()
        startLocationAlert()
    }

    private fun startLocationAlert() {
        myLocationCallback = createLocationCallback()
        fusedLocationClient.requestLocationUpdates(locationRequest!! , myLocationCallback, Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        stopLocationAlert()
    }

    private fun stopLocationAlert() {
        fusedLocationClient.removeLocationUpdates(myLocationCallback!!)
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestLocationPermisson() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), permissonId
        )
    }

    public fun updateUI(addressList: kotlin.collections.MutableList<Address>?) {
        mainBinding!!.currnLocationTv.setText(addressList!!.get(0).countryName)
        mainBinding!!.latitudeTv.setText(addressList.get(0).latitude.toString())
        mainBinding!!.longitudeTv.setText(addressList.get(0).longitude.toString())
        mainBinding!!.addressTv.setText(addressList.get(0).countryCode)
        mainBinding!!.cityTv.setText(addressList.get(0).locality)
        mainBinding!!.countryTv.setText(addressList.get(0).postalCode)
    }

    private fun isLocatiponPermissionGranted(): Pair<Boolean, Int> {
        var result = Pair(false, 0)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = Pair(true, 1)
            return result
        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = Pair(true, 2)
            return result
        } else
            return result

    }

    private fun createLocationRequest():LocationRequest? = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        isWaitForAccurateLocation = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _mainBinding = null
    }

    private fun createLocationCallback(): LocationCallback{
        return object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                for (location in p0.locations){

                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    Log.e("$$$$$", "onLocationResult: $address" )
                    viewModel.setViewModelLocation(address!!)


//               val mainActivity = MainActivity()
//               mainActivity.updateUI(address)
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
                Log.e("!!!!!!!!!!!", "onLocationResult: $p0" )
            }
        }
    }

//   class MyLocationCallback (private val  context: Context): LocationCallback() {
//       override fun onLocationResult(p0: LocationResult) {
//           super.onLocationResult(p0)
//           for (location in p0.locations){
//
//               val geocoder = Geocoder(context, Locale.getDefault())
//               val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//               Log.e("$$$$$", "onLocationResult: $address" )
//
//
////               val mainActivity = MainActivity()
////               mainActivity.updateUI(address)
//           }
//       }
//
//       override fun onLocationAvailability(p0: LocationAvailability) {
//           super.onLocationAvailability(p0)
//
//           Log.e("!!!!!!!!!!!", "onLocationResult: $p0" )
//
//       }
//
//   }
}