package pt.ipt.dam.pokeWeather.retrofit
import WeatherService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInitializer {

    //private val weatherApiKey = "621458b7064959935ab4b148a9f412c2"
    private val weatherHost= "https://api.openweathermap.org/"
    //data/2.5/weather?lat={lat}&lon={lon}&appid=$weatherApiKey"
    private val pokeHost = "https://pokeapi.co/"
    //api/v2/pokemon/
    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofitWeather =
        Retrofit.Builder()
            .baseUrl(weatherHost)
            .addConverterFactory( GsonConverterFactory.create(gson))
            .build()

    private val retrofitPoke =
        Retrofit.Builder()
            .baseUrl(pokeHost)
            .addConverterFactory( GsonConverterFactory.create(gson))
            .build()

    fun weatherService(): WeatherService = retrofitWeather.create(WeatherService::class.java)
    fun pokeService() = retrofitPoke.create(WeatherService::class.java)
}