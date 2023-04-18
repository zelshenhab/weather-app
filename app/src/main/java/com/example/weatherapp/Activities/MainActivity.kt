package com.example.weatherapp.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.Models.WeatherModel
import com.example.weatherapp.R
import com.example.weatherapp.Utilites.ApiUtilities
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import java.text.SimpleDateFormat
import java.util.Date

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

                    binding.citySearch.clearFocus()
                }
                return@setOnEditorActionListener true
            }
     else{
                return@setOnEditorActionListener false


    }



        }

        binding.currentLocation.setOnClickListener {


            getCurrentLocation()

        }


    }

    private fun getCityWeather(city:String){

        binding.progressBar.visibility= View.VISIBLE

        ApiUtilities.getApiInterface()?.getCityWeatherData(city,apiKey)?.enqueue(
            object :Callback<WeatherModel>{
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {

                    if (response.isSuccessful){

                        binding.progressBar.visibility= View.GONE

                        response.body()?.let {

                            setData(it)
                        }
                    }
                    else{

                        Toast.makeText(this@MainActivity, "No City Found", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility= View.GONE
                    }

                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {

                }


            }
        )

    }

    private fun fetchCurrentLocationWeather(latitude:String,longitude:String){

        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude,longitude,apiKey)
            ?.enqueue(object :Callback<WeatherModel>{
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful){

                        binding.progressBar.visibility= View.GONE

                        response.body()?.let {

                            setData(it)
                        }



                    }


                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    TODO("Not yet implemented")
                }


            })

    }

    private fun getCurrentLocation(){

        if (checkPermissions()){

            if (isLocationEnabled()){

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )!=PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )!=PackageManager.PERMISSION_GRANTED
                ){

                    requestPermissions()

                    return
                }

                fusedLocationProvider.lastLocation
                    .addOnSuccessListener { location->

                        if (location!=null){

                            currentLocation=location

                            binding.progressBar.visibility= View.VISIBLE

                            fetchCurrentLocationWeather(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )


                        }


                    }

            }

            else{

                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

                startActivity(intent)
            }

        }

        else{

            requestPermission()
        }




    }

    private fun requestPermission(){

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )


    }

    private fun isLocationEnabled():Boolean{

        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE)
        as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun checkPermissions():Boolean{

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )==PackageManager.PERMISSION_GRANTED){

            return true


        }

        return false


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode==LOCATION_REQUEST_CODE){


            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)


                getCurrentLocation()


        }
        else{



        }
    }

    private fun setData(body:WeatherModel){

        binding.apply {

        val currentDate= SimpleDateFormat("dd//MM/yyyy hh:mm").format(Date())

            dateTime.text=currentDate.toString()

            maxTemp

        }

    }


}