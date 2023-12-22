package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.model.UserDetails
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
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email e/ou password não pode(m) estar vazios!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = UserLoginRequest(email, password)
        RetrofitInitializer().UserService().loginUser(loginRequest).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    // Retrieve the user ID from the login response
                    val pessoaID = response.body()?.pessoaID
                    // Now that login is successful, fetch the person details
                    pessoaID?.let { id ->
                        getPessoaDetail(id)
                    }

                    Toast.makeText(this@LoginActivity, "Login bem sucedido!", Toast.LENGTH_LONG).show()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("erro login","Login failed - $errorMessage")
                    Toast.makeText(this@LoginActivity, "Login falhou - $errorMessage}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Login Falhou: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getPessoaDetail(pessoaID: Int) {
        RetrofitInitializer().UserService().getPessoaDetails(pessoaID).enqueue(object : Callback<UserDetails> {
            override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {
                if (response.isSuccessful) {
                    // Successfully retrieved user details, now get the name
                    val userDetails = response.body()
                    userDetails?.let {
                        navigateToPokeWeatherActivity(it.nome)
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Failed to get user details", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserDetails>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error fetching details: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToPokeWeatherActivity(userName: String) {
        val intent = Intent(this, PokeWeatherActivity::class.java).apply {
            putExtra("USER_NAME", userName)
        }
        startActivity(intent)
        finish()
    }
}