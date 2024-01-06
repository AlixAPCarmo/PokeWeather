package pt.ipt.dam.pokeWeather.model

data class WeatherResponse (
    val main: Main,
    val sys: Sys,
    val weather: List<Weather>,
    val name: String,
    val coord: Coord,
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

