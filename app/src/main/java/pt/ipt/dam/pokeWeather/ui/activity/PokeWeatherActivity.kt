package pt.ipt.dam.pokeWeather.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import pt.ipt.dam.pokeWeather.model.WeatherResponse
import pt.ipt.dam.pokeWeather.R

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class PokeWeatherActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager

    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    //places api key
    //AIzaSyAGQwWNMkKqM-QEvsafZe6Ym6bpbm6E97w

    private lateinit var btnAboutUs: Button
    private lateinit var btnItemList: Button
    private lateinit var btnLogout:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLocation()

        btnAboutUs = findViewById(R.id.aboutUsbtn)
        btnAboutUs.setOnClickListener {
            val intent = Intent(this, AboutUs::class.java)
            startActivity(intent)
            finish()
        }

        btnItemList = findViewById(R.id.itemListbtn)
        btnItemList.setOnClickListener {
            if (!isUserLoggedIn()) {
                Snackbar.make(it, "É necessário efetuar login para aceder a esta funcionalidade", Snackbar.LENGTH_LONG)
                    .setAction("Login") {
                        navigateToLoginActivity()
                    }.show()
            } else {
                val intent = Intent(this, CrudList::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnLogout = findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            if (isUserLoggedIn()) {
                logoutUser()
            } else {
                navigateToLoginActivity()
            }
        }
    }

    private fun getTemperature(lat: Double, long: Double) {
        // Log.e("gt coords", "$lat, $long")
        RetrofitInitializer().weatherService().getWeather(lat, long)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    // Check if the user is logged in
                    val isAuthenticated = isUserLoggedIn()
                    // get temp from api
                    val temperature = response.body()?.main?.temp
                    // Convert from Kelvin to Celsius
                    val temperatureCelsius = temperature?.minus(273.15)?.roundToInt().toString()
                    val temperatureFeelsLike = response.body()?.main?.feels_like?.minus(273.15)?.roundToInt().toString()
                    val tempMin = response.body()?.main?.temp_min?.minus(273.15)?.roundToInt().toString()
                    val tempMax = response.body()?.main?.temp_max?.minus(273.15)?.roundToInt().toString()

                    // weather icon
                    val iconTemp = response.body()?.weather?.last()?.icon.toString()
                    getWeatherImage(iconTemp)

                    // get local name from api
                    val local = response.body()?.name

                    // get country code from api
                    val countryCode = response.body()?.sys?.country

                    // weather description
                    val descTemp = response.body()?.weather?.first()?.description

                    // assign the views to the updates text values
                    findViewById<TextView>(R.id.tempView).text = (" $temperatureCelsius ºC")
                    findViewById<TextView>(R.id.status).text = (" $descTemp")

                    tvGpsLocation = findViewById(R.id.textView)
                    tvGpsLocation.text = ("$local , $countryCode")
                    findViewById<TextView>(R.id.tvTempMin).text = "Temp. Mínima: $tempMin°C"
                    findViewById<TextView>(R.id.tvTempMax).text = "Temp. Máxima: $tempMax°C"

                    // Update UI based on user authentication
                    findViewById<TextView>(R.id.tvFeelsLike).apply {
                        visibility = View.VISIBLE
                        text = if (isAuthenticated) "$temperatureFeelsLike°C" else "Login Necessário"
                    }
                    findViewById<TextView>(R.id.tvPressure).apply {
                        visibility = View.VISIBLE
                        text = if (isAuthenticated) "${response.body()?.main?.pressure} hPa" else "Login Necessário"
                    }
                    findViewById<TextView>(R.id.tvHumidity).apply {
                        visibility = View.VISIBLE
                        text = if (isAuthenticated) "${response.body()?.main?.humidity}%" else "Login Necessário"
                    }

                    // console logs
                    Log.e("getTemperature", "$temperature")
                    Log.e("getTemperature", "$temperatureCelsius")
                    Log.e("getTemperature", "$lat,$long")
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("getTemperature", "Call failed with error: ${t.message}")
                }
            })
    }



    //code for this function obtained at:
    //https://www.geeksforgeeks.org/how-to-load-any-image-from-url-without-using-any-dependency-in-android/
    fun getWeatherImage(id: String) {
        // Declaring and initializing the ImageView
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Declaring executor to parse the URL
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            // Image URL
            val imageURL = "https://openweathermap.org/img/wn/$id.png"

            try {
                val `in` = java.net.URL(imageURL).openStream()
                val image = BitmapFactory.decodeStream(`in`)

                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                handler.post {
                    Toast.makeText(this@PokeWeatherActivity, "Failed to load image.", Toast.LENGTH_SHORT).show()
                }
                Log.e("getWeatherImage", "Failed to load image: $e")
            }
        }
    }


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)

    }

    override fun onLocationChanged(location: Location) {
        //tvGpsLocation = findViewById(R.id.textView)
        //tvGpsLocation.text =
        //"Latitude: " + location.latitude + " , Longitude: " + location.longitude
        val lat = location.latitude
        val long = location.longitude
        Log.e("Coordinates", "$lat, $long")
        getTemperature(lat, long)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "")
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0)
        val currentTime = System.currentTimeMillis()
        return token?.isNotEmpty() == true && currentTime < expirationTime
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        sharedPreferences.edit().remove("auth_token").remove("token_expiration_time").apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }



}