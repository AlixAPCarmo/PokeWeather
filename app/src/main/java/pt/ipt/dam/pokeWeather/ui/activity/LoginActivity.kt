package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.model.UserLoginRequest
import pt.ipt.dam.pokeWeather.model.UserLoginResponse
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = UserLoginRequest(email, password)
        RetrofitInitializer().UserService().loginUser(loginRequest).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveAuthToken(it.token)
                        navigateToPokeWeatherActivity(it.userName)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@LoginActivity, "Login falhou - $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Login falhou: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    private fun navigateToPokeWeatherActivity(userName: String) {
        val intent = Intent(this, PokeWeatherActivity::class.java).apply {
            putExtra("USER_NAME", userName)
        }
        startActivity(intent)
        finish()
    }
}
