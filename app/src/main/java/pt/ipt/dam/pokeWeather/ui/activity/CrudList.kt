package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.dam.pokeWeather.R

class CrudList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_list)

        val btnReturn = findViewById<Button>(R.id.returnBtn)

        btnReturn.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent);
            finish();
        }
    }
}