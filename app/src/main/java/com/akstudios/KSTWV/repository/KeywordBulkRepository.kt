package com.akstudios.KSTWV.repository

import com.akstudios.KSTWV.network.ApiService
import com.seo.app.seostudio.model.KeywordItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class KeywordBulkRepository @Inject constructor(var api: ApiService) {
    fun getKeywordBulk(url:String) = flow<Response<HashMap<String, KeywordItem>>> {
        emit(api.getKeywordBulk(url))
    }.flowOn(Dispatchers.IO)
}