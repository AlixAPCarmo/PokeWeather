package pt.ipt.dam.pokeWeather.retrofit
import pt.ipt.dam.pokeWeather.retrofit.service.WeatherService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pt.ipt.dam.pokeWeather.retrofit.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInitializer {

    // api hosts
    private val weatherHost= "https://api.openweathermap.org/"
    private val pokeHost = "https://pokeapi.co/"
    private val userHost = "https://alix-dweb.azurewebsites.net/"

    // allow json parsing
    private val gson: Gson = GsonBuilder().setLenient().create()

    // initialization of retrofit for the different hosts
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

    private val retrofitUser =
        Retrofit.Builder()
            .baseUrl(userHost)
            .addConverterFactory( GsonConverterFactory.create(gson))
            .build()

    // implementation of the retrofit using the services
    fun weatherService(): WeatherService = retrofitWeather.create(WeatherService::class.java)
    fun pokeService() = retrofitPoke.create(WeatherService::class.java)
    fun UserService(): UserService = retrofitUser.create(UserService::class.java)
}