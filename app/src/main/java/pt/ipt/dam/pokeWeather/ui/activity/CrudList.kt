package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dalvik.annotation.optimization.CriticalNative
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.WeatherAdapter
import pt.ipt.dam.pokeWeather.model.WeatherResponse
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class CrudList : AppCompatActivity() {

    // variable declaration
    private lateinit var searchBar: SearchView
    private lateinit var weatherList: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var btnReturn: Button

    companion object {
        private const val KEY_WEATHER_LIST = "weatherList"
        private const val SHARED_PREF_NAME = "WeatherPrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_list)

        searchBar = findViewById(R.id.searchBar)
        weatherList = findViewById(R.id.weatherList)
        weatherAdapter = WeatherAdapter()
        weatherList.adapter = weatherAdapter
        weatherList.layoutManager = LinearLayoutManager(this)

        // implements return button
        btnReturn = findViewById(R.id.returnBtn)
        btnReturn.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        //implements search bar
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (isValidQuery(it)) {
                        performWeatherSearch(it)
                        searchBar.clearFocus()
                    } else {
                        Toast.makeText(this@CrudList, "Erro no formato", Toast.LENGTH_LONG).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })

        weatherAdapter.setOnItemClickListener(object : WeatherAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                //Toast.makeText(this@CrudList, "Edit clicked at position $position", Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteClick(position: Int) {
                // Handle the delete action
                weatherAdapter.removeWeatherItem(position)
                saveWeatherListToSharedPreferences()
            }

            override fun onSaveClick(position: Int, newText: String) {
                if (isValidQuery(newText)){
                    // Update the weather item in the adapter
                    weatherAdapter.updateWeatherItem(position, newText)

                    // Exit edit mode for the updated item
                    weatherAdapter.exitEditMode(position)

                    performWeatherSearch(newText)
                    weatherAdapter.removeWeatherItem(position)
                }else {
                    Toast.makeText(this@CrudList, "Formato inválido. Por favor, use o formato (CIDADE,COD PAIS)", Toast.LENGTH_SHORT).show()
                }

            }
        })
        // gets the weather list from shared pref
        loadWeatherListFromSharedPreferences()
    }

    override fun onPause() {
        super.onPause()
        // Save weather list to SharedPreferences
        saveWeatherListToSharedPreferences()
    }

    // gets weather lsit from shared pref
    private fun loadWeatherListFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val weatherListSet = sharedPreferences.getStringSet(KEY_WEATHER_LIST, HashSet())
        val weatherList = weatherListSet?.toList() ?: emptyList()
        weatherAdapter.setWeatherItems(weatherList)
    }

    // save weather lsit to shared pref
    private fun saveWeatherListToSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val weatherList = weatherAdapter.getWeatherItems()
        editor.putStringSet(KEY_WEATHER_LIST, HashSet(weatherList))
        editor.apply()
    }

    //checks if the query on search bar follows the (city, country code)
    private fun isValidQuery(query: String): Boolean {
        val parts = query.split(",")
        return parts.size == 2 && parts[1].length <= 3
    }

    // gets weather from the user location input
    private fun performWeatherSearch(query: String) {
        RetrofitInitializer().weatherService().getWeatherCity(query)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        val tempInCelsius = weatherData?.main?.temp?.minus(273.15)?.roundToInt().toString()
                        val displayText = "${weatherData?.name}: $tempInCelsius°C"

                        runOnUiThread {
                            weatherAdapter.addWeatherItem(displayText)
                            searchBar.setQuery("", false)
                        }

                        Log.d("WeatherSearch", "Added to adapter: $displayText")

                        // Log the current items in the adapter
                        Log.d("WeatherSearch", "Current items in adapter: ${weatherAdapter.itemCount}")
                    } else {
                        Toast.makeText(this@CrudList, "Error in response: ${response.code()}", Toast.LENGTH_LONG).show()
                        Log.e("WeatherSearch", "Error in response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(this@CrudList, "Failed to retrieve data", Toast.LENGTH_LONG).show()
                    Log.e("WeatherSearch", "Failed to retrieve data", t)
                }
            })
    }

}