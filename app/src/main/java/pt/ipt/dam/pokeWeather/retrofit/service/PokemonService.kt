package pt.ipt.dam.pokeWeather.retrofit.service

import pt.ipt.dam.pokeWeather.model.Pokemon
import retrofit2.Call
import retrofit2.http.*

interface PokemonService {
    @GET("api/v2/pokemon/{name}")
    fun getPokemon(
        @Path("name") name: String
    ): Call<Pokemon>
}