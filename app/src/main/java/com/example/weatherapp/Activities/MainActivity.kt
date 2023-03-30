package com.example.weatherapp.Activities

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding

    private lateinit var currentLocation: Location

    private lateinit var fusedLocationProvider:FusedLocationProviderClient

    private val LOCATION_REQUEST_CODE=101

    private val apiKey="f70ca239bf30695349b25a9bb3361c69"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        fusedLocationProvider=LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        binding.citySearch.setOnEditorActionListener { textView, i, keyEvent ->

            if (i==EditorInfo.IME_ACTION_SEARCH){
                getCityWeather(binding.citySearch.text.toString())

                val view=this.currentFocus

                if (view!=null){

                    val imm:InputMethodManager= getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    imm.hideSoftInputFromWindow(view.windowToken,0)


                }


            }


        }



    }
}