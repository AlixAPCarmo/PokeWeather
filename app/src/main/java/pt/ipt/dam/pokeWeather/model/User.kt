package pt.ipt.dam.pokeWeather.model

// data class for Register activity
data class UserRegister(
    val Nome: String,
    val Email: String,
    val Password: String,
)

// data class for Login request activity
data class UserLoginRequest(
    val Email: String,
    val Password: String
)


// data class to handle user login response
data class UserLoginResponse(
    val token: String,
    val userName: String
)
