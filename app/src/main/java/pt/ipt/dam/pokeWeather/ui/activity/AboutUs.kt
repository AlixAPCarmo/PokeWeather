package pt.ipt.dam.pokeWeather.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.dam.pokeWeather.R

//about us activity
class AboutUs : AppCompatActivity() {

    // variable declaration
    private lateinit var btnReturn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set view to activity About Us
        setContentView(R.layout.activity_about_us)

        // find and start of a new activity on btnReturn
        btnReturn = findViewById(R.id.returnBtn)
        btnReturn.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}