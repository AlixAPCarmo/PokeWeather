package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Context
import pt.ipt.dam.pokeWeather.model.WeatherResponse
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.WeatherAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrudList : AppCompatActivity() {

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

        btnReturn = findViewById(R.id.returnBtn)
        btnReturn.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (isValidQuery(it)) {
                        // Assuming performWeatherSearch is a placeholder for your logic
                        performWeatherSearch(it)
                        searchBar.clearFocus()
                    } else {
                        Toast.makeText(this@CrudList, "Error in format", Toast.LENGTH_LONG).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // Set the listener for item clicks
        weatherAdapter.setOnItemClickListener(object : WeatherAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                // Handle the edit action here
                // For example, show an edit dialog or navigate to an edit screen
                Toast.makeText(this@CrudList, "Edit clicked at position $position", Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteClick(position: Int) {
                // Handle the delete action here
                weatherAdapter.removeWeatherItem(position)
            }

            override fun onSaveClick(position: Int, newText: String) {
                // Handle the save action here
                weatherAdapter.updateWeatherItem(position, newText)
                // Exit edit mode
                weatherAdapter.exitEditMode(position)
            }
        })

        // Load weather list from SharedPreferences
        loadWeatherListFromSharedPreferences()
    }

    override fun onPause() {
        super.onPause()
        // Save weather list to SharedPreferences
        saveWeatherListToSharedPreferences()
    }

    private fun loadWeatherListFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val weatherListSet = sharedPreferences.getStringSet(KEY_WEATHER_LIST, HashSet())
        val weatherList = weatherListSet?.toList() ?: emptyList()

        weatherAdapter.setWeatherItems(weatherList)
    }

    private fun saveWeatherListToSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val weatherList = weatherAdapter.getWeatherItems()
        editor.putStringSet(KEY_WEATHER_LIST, HashSet(weatherList))
        editor.apply()
    }

    private fun isValidQuery(query: String): Boolean {
        val parts = query.split(",")
        return parts.size == 2 && parts[1].length <= 3
    }

    private fun performWeatherSearch(query: String) {
        RetrofitInitializer().weatherService().getWeatherCity(query)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        val tempInCelsius = kelvinToCelsius(weatherData?.main?.temp)
                        val displayText = "${weatherData?.name}: $tempInCelsius°C"

                        runOnUiThread {
                            weatherAdapter.addWeatherItem(displayText)
                            searchBar.setQuery("", false)
                        }

                        Log.d("WeatherSearch", "Added to adapter: $displayText")

                        // Log the current items in the adapter
                        Log.d("WeatherSearch", "Current items in adapter: ${weatherAdapter.itemCount}")
                    } else {
                        Toast.makeText(this@CrudList, "Erro no formato", Toast.LENGTH_LONG).show()
                        Log.e("WeatherSearch", "Error in response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(this@CrudList, "Erro no formato", Toast.LENGTH_LONG).show()
                    Log.e("WeatherSearch", "Failed to retrieve data", t)
                }
            })
    }

    private fun kelvinToCelsius(tempInKelvin: Double?): String {
        return if (tempInKelvin != null) {
            "%.0f".format(tempInKelvin - 273.15)
        } else {
            "N/A"
        }
    }
}
