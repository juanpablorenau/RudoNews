package com.example.rudonews.api

import com.example.rudonews.App
import com.example.rudonews.api.Config.HTTP_CLIENT_AUTHORIZATION
import com.example.rudonews.api.Config.TYPE_ITEM_AUTHORIZATION
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = App.preferences.getAccessToken()
        val request = newRequestWithAccessToken(chain.request(), accessToken.toString())
        return chain.proceed(request)
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header(TYPE_ITEM_AUTHORIZATION, "$HTTP_CLIENT_AUTHORIZATION $accessToken")
            .build()
    }
}
