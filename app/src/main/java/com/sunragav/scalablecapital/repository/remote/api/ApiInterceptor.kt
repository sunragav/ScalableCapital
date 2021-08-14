package com.sunragav.scalablecapital.repository.remote.api

import com.sunragav.scalablecapital.app.di.AuthToken
import com.sunragav.scalablecapital.app.di.AuthTokenPrefix
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiInterceptor @Inject constructor(
    @AuthTokenPrefix private val prefix: String,
    @AuthToken private val authHeader: String
) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader(AUTHORIZATION_HEADER, "$prefix $authHeader")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}