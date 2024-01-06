package pt.ipt.dam.pokeWeather.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class WeatherItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val temperature: String
)

// WeatherDao.kt
// @Dao
interface WeatherDao {
    @Insert
    suspend fun insert(weatherItem: WeatherItem)

    @Query("SELECT * FROM WeatherItem")
    suspend fun getAllWeatherItems(): List<WeatherItem>
}