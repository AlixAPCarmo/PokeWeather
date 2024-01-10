package pt.ipt.dam.pokeWeather.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import pt.ipt.dam.pokeWeather.R
import pt.ipt.dam.pokeWeather.model.UserRegister
import pt.ipt.dam.pokeWeather.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback

//Register activity
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set view to register activity
        setContentView(R.layout.activity_register)

        //get the items from the layout
        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        // when register button is pressed it calls the registerUser function
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            //local validation for password
            val passwordErr = validatePassword(password)
            if (passwordErr.isNotEmpty()) {
                passwordErr.forEach { error ->
                    Toast.makeText(this@RegisterActivity, error, Toast.LENGTH_LONG).show()
                }
            } else {
                registerUser(name, email, password)
            }
        }

        // redirects to login activity when login button is pressed
        loginButton.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // function that implements register and retrofill call to the api
    private fun registerUser(name: String, email: String, password: String) {
        // initializes the retrofit user service
        val userService = RetrofitInitializer().UserService()
        // calls the user register fun
        val userRegister = UserRegister(name, email, password)

        // retrofit call
        userService.registerUser(userRegister).enqueue(object : Callback<UserRegister> {

            // if response is 20x
            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                if (response.isSuccessful) {
                    // shows a success message to user and redirects to login view
                    Toast.makeText(this@RegisterActivity, "Bem Vindo ${userRegister.Nome}", Toast.LENGTH_SHORT).show()
                    //Log.e("RegisterActivity", "UserRegister registered successfully.")
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    // if response not 20x shows the errors from api
                    response.errorBody()?.let { errorBody ->
                        val errorString = errorBody.string()
                        Log.e("RegisterActivity", "Corpo do erro: $errorString")

                        parseAndShowErrorMessages(errorString)
                    }
                }
            }

            // if retrofit cannot establish a connection
            override fun onFailure(call: Call<UserRegister>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                //Log.e("RegisterActivity", "Registration failed with exception: ${t.message}")
            }
        })

    }

    //shows user the validation errors from api to username and email
    // this code is from stackoverflow.
    private fun parseAndShowErrorMessages(errorString: String) {
        try {
            val jsonObject = JSONObject(errorString)
            val errorMessages = mutableListOf<String>()

            if (jsonObject.has("errors")) {
                val errorsObj = jsonObject.getJSONObject("errors")

                errorsObj.keys().forEach { key ->
                    val errorArray = errorsObj.getJSONArray(key)
                    for (i in 0 until errorArray.length()) {
                        errorMessages.add("$key: ${errorArray.getString(i)}")
                    }
                }
            }

            // show user the error message
            errorMessages.forEach { errorMessage ->
                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        } catch (e: JSONException) {
            Log.e("RegisterActivity", "Erro: $e")
        }
    }

    // function to validate password errors locally
    private fun validatePassword(password: String): List<String> {
        val errors = mutableListOf<String>()

        if (password.length < 6) {
            errors.add("A senha deve ter pelo menos 6 caracteres.")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("A senha deve ter pelo menos um dígito ('0'-'9').")
        }

        if (!password.any { it.isLowerCase() }) {
            errors.add("A senha deve ter pelo menos uma letra minúscula ('a'-'z').")
        }

        if (password.toSet().size < 2) {
            errors.add("A senha deve usar pelo menos 1 caractere diferente.")
        }

        return errors
    }


}