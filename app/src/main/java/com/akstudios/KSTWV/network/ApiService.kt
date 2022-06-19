package com.akstudios.KSTWV.network

import com.seo.app.seostudio.model.KeywordItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url


interface ApiService {
    @Headers("Accept: application/json")
    @GET
    suspend fun getKeywordBulk(
        @Url url: String
    ): Response<HashMap<String, KeywordItem>>

}
