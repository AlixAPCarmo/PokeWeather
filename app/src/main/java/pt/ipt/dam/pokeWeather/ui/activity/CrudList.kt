package pt.ipt.dam.pokeWeather.ui.activity

import pt.ipt.dam.pokeWeather.model.WeatherResponse
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import pt.ipt.dam.pokeWeather.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrudList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_list)

        val searchBar = findViewById<SearchView>(R.id.searchBar)
        val weatherInfo = findViewById<TextView>(R.id.weatherInfo)
        val btnReturn = findViewById<Button>(R.id.returnBtn)

        btnReturn.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (isValidQuery(it)) {
                        performWeatherSearch(it, weatherInfo)
                    } else {
                        Toast.makeText(this@CrudList, "Erro no formato",Toast.LENGTH_LONG)
                    }
                }
                searchBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun isValidQuery(query: String): Boolean {
        val parts = query.split(",")
        return parts.size == 2 && parts[1].length <= 3
    }

    private fun performWeatherSearch(query: String, weatherInfo: TextView) {
        RetrofitInitializer().weatherService().getWeatherCity(query).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val tempInCelsius = kelvinToCelsius(weatherData?.main?.temp)
                    val displayText = "${weatherData?.name}: $tempInCelsius°C"
                    weatherInfo.text = displayText
                } else {
                    Toast.makeText(this@CrudList, "Erro no formato",Toast.LENGTH_LONG)
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@CrudList, "Erro no formato",Toast.LENGTH_LONG)

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
