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

// login activity
class LoginActivity : AppCompatActivity() {

    // variable declaration
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var tvContinueWithoutLogin: TextView
    private lateinit var btnRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set view to activity login
        setContentView(R.layout.activity_login)

        // find items by id and associate them to a variable
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        tvContinueWithoutLogin = findViewById(R.id.tvContinueWithoutLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // check if user is already logged in
        if (isUserLoggedIn()) {
            // navigate to next activity
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Implementation of login logic when the login button is pressed
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginUser(email, password)
        }

        // Implementation of login when the the textview Continue without login is pressed
        tvContinueWithoutLogin.setOnClickListener {
            val intent = Intent(this, PokeWeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Implementation of register logic when register button is pressed
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // function that implements login and retrofit call to the Login api endpoint
    private fun loginUser(email: String, password: String) {
        // login request data class implementation
        val loginRequest = UserLoginRequest(email, password)
        // retrofit call
        RetrofitInitializer().UserService().loginUser(loginRequest)
            .enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    //if api response 20x then it gets is response
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        // saves the token and user name and go to next view
                        loginResponse?.let {
                            saveAuthToken(it.token)
                            saveUserNameFromJWT(it.token)
                            val intent = Intent(this@LoginActivity, PokeWeatherActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        //gets the error from the api
                        val errorMessage = response.errorBody()?.string() ?: "Erro"
                        // toast message in case login fails
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Falhou - $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // show a toast message if a connection to api isnt possible
                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login falhou: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    //save jwt token to shared preferences as auth_token and the expiration time
    private fun saveAuthToken(token: String) {
        val jwt = JWT(token)
        val expiration = jwt.expiresAt?.time ?: System.currentTimeMillis()
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.putLong("token_expiration_time", expiration)
        editor.apply()
    }

    // saves the name from jwt token and saves as unique_name on shared preferences
    private fun saveUserNameFromJWT(token: String) {
        val jwt = JWT(token)
        val uniqueName = jwt.getClaim("unique_name").asString()
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_name", uniqueName)
        editor.apply()
    }

    // function to check if user is logged in by checking if theres any token saved on shared pref
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "")
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0)
        val currentTime = System.currentTimeMillis()
        return token?.isNotEmpty() == true && currentTime < expirationTime
    }

}
