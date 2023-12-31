package pt.ipt.dam.pokeWeather.model

data class UserRegister(
    val Nome: String,
    val Email: String,
    val Password: String,
    val Telefone: String,
)

data class UserLoginRequest(
    val Email: String,
    val Password: String
)


data class UserLoginResponse(
    val token: String,
    val userName: String
)
/*
data class UserDetails(
    val pessoaID: Int,
    val nome: String
)*/