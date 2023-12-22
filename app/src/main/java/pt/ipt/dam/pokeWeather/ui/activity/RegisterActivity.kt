package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Response
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.model.UserRegister
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)

        val registerButton = findViewById<Button>(R.id.buttonRegister)
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phone = phoneEditText.text.toString()

            registerUser(name, email, password, phone)
        }
    }

    private fun registerUser(name: String, email: String, password: String, phone: String) {
        val userService = RetrofitInitializer().UserService()
        val userRegister = UserRegister(name, email, password, phone)

        userService.registerUser(userRegister).enqueue(object : Callback<UserRegister> {
            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Welcolme ${userRegister.Nome}", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "UserRegister registered successfully.")
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "Registration failed with response code: ${response.code()} and message: ${response.message()}")
                    response.errorBody()?.let {
                        val errorString = it.string()
                        Log.e("RegisterActivity", "Error body: $errorString")
                    }
                }
            }

            override fun onFailure(call: Call<UserRegister>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterActivity", "Registration failed with exception: ${t.message}")
            }
        })
    }

}