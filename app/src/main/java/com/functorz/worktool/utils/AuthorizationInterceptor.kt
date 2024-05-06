package com.functorz.worktool.utils

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .build()
        return chain.proceed(request)
    }
}