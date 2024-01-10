package pt.ipt.dam.pokeWeather.model

// data class to handle response from api with specific keys
// main, sys, weather and coord are the main key from the api response
data class WeatherResponse (
    val main: Main,
    val sys: Sys,
    val weather: List<Weather>,
    val name: String,
    val coord: Coord,
)

// main data class with the response from the call to the weather api
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)


data class Sys(
    val type: Int,
    val id: Long,
    val sunrise: Long,
    val sunset: Long,
    val country: String
)

data class Weather(
    val description: String,
    val icon: String
)

data class Coord(
    val lon: Double,
    val lat: Double
)

