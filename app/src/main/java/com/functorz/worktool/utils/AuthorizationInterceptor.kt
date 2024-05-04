package com.functorz.worktool.utils

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJkZWZhdWx0Um9sZSI6InVzZXIiLCJyZiI6MTcxNDkyMjAzOSwiWkVST19VU0VSX0lEIjoiMTAwMDAwMDAwMDAwMDAwNCIsImF1dGhlbnRpY2F0aW9uVHlwZSI6IlBIT05FX05VTUJFUl9QQVNTV09SRCIsInplcm8iOnt9LCJyb2xlcyI6WyJ1c2VyIiwic2VsZiJdLCJpc3MiOiIxMDAwMDAwMDAwMDAwMDAwIiwic3ViIjoiMTAwMDAwMDAwMDAwMDAwNCIsImlhdCI6MTcxNDc0OTIzOSwiZXhwIjoxNzE0ODM1NjM5fQ.CorYUPJtZyYzy1tgqx-ORNL4rOBXX5DRty3UVGhgOYY")
            .build()

        return chain.proceed(request)
    }
}