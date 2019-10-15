package com.pertamina.spbucare.network

import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SpbuCareApiService {
    @GET("auth/{email}/{password}")
    fun createAccountAsync(@Path("email") email: String, @Path("password") password: String): Deferred<String>

    @POST("auth/{uid}/{email}/{password}")
    fun updateAccountAsync(@Path("uid") uid: String, @Path("email") email: String, @Path("password") password: String): Deferred<String>

    @POST("user/{id}")
    fun createUserDataAsync(@Path("id") userId: String, @Body user: User): Deferred<User>

    @POST("Order/{id}")
    fun createOrderManualAsync(@Path("id") userId: String, @Body Order: Order): Deferred<Order>
}