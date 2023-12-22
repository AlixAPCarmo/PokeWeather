package pt.ipt.dam.pokeWeather.retrofit.service

import pt.ipt.dam.pokeWeather.model.UserDetails
import pt.ipt.dam.pokeWeather.model.UserLoginRequest
import pt.ipt.dam.pokeWeather.model.UserLoginResponse
import pt.ipt.dam.pokeWeather.model.UserRegister
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @Headers("Content-Type: application/json")
    @POST("api/PessoasAPI/create")
    fun registerUser(@Body userRegisterData: UserRegister): Call<UserRegister>

    @Headers("Content-Type: application/json")
    @POST("api/PessoasAPI/login")
    fun loginUser(@Body loginRequest: UserLoginRequest): Call<UserLoginResponse>

    @GET("api/PessoasAPI/{id}")
    fun getPessoaDetails(@Path("id") pessoaId: Int): Call<UserDetails>

}
