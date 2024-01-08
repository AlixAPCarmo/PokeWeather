package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.model.UserLoginRequest
import pt.ipt.dam.pokeWeather.model.UserLoginResponse
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var tvContinueWithoutLogin: TextView
    private lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        tvContinueWithoutLogin = findViewById(R.id.tvContinueWithoutLogin)
        btnRegister = findViewById(R.id.btnRegister)

        if (isUserLoggedIn()) {
            navigateToPokeWeatherActivity()
        } else {
            if (isTokenExpired()) {
                Toast.makeText(this, "Sem sessão iniciada", Toast.LENGTH_LONG).show()
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginUser(email, password)
        }

        tvContinueWithoutLogin.setOnClickListener {
            navigateToPokeWeatherActivity()
        }

        btnRegister.setOnClickListener {
            // Inicie a Activity de Registro aqui
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = UserLoginRequest(email, password)
        RetrofitInitializer().UserService().loginUser(loginRequest)
            .enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            saveAuthToken(it.token)
                            navigateToPokeWeatherActivity()
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Erro"
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Falhou - $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login falhou: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun saveAuthToken(token: String) {
        val jwt = JWT(token)
        val expiration = jwt.expiresAt?.time ?: System.currentTimeMillis()
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.putLong("token_expiration_time", expiration)
        editor.apply()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "")
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0)
        val currentTime = System.currentTimeMillis()
        return token?.isNotEmpty() == true && currentTime < expirationTime
    }

    private fun isTokenExpired(): Boolean {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0)
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= expirationTime
    }

    private fun navigateToPokeWeatherActivity() {
        val intent = Intent(this, PokeWeatherActivity::class.java)
        startActivity(intent)
        finish()
    }
}
