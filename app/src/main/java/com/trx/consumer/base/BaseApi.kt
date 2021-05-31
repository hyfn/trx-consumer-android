package com.trx.consumer.base

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface BaseApi {

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body params: HashMap<String, Any>
    ): Response<Any>

    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @QueryMap options: HashMap<String, Any>
    ): Response<Any>

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body params: HashMap<String, Any>
    ): Response<Any>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body params: HashMap<String, Any>
    ): Response<Any>

    // Todo: May need to use @HTTP(method = "PATCH"), needs investigation.
    @PATCH
    suspend fun patch(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body params: HashMap<String, Any>
    ): Response<Any>
}
