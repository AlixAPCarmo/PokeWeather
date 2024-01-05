package pt.ipt.dam.pokeWeather.model

data class WeatherResponse (
    val main: Main,
    val sys: Sys,
    val weather: List<Weather>,
    val name: String

)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Sys(
    val country: String
)

data class Weather(
    val description: String,
    val icon: String
)