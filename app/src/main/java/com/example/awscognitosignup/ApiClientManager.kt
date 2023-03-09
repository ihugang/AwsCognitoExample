package com.example.awscognitosignup

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody.Companion.toResponseBody
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

class ApiClientManager {
    private var retrofit: Retrofit
    private var logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.i("interceptor msg :%s", message)
        }
    })

    private val okHttpClient = OkHttpClient().newBuilder()//.addInterceptor(logging)
        //  .addInterceptor(ParamsInterceptor())
        .addInterceptor(ResponseInterceptor())
        .build()

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("http://54.92.87.233:30002/")
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    companion object {
        private val manager = ApiClientManager()
        val client: Retrofit
            get() = manager.retrofit
    }
}

class ResponseInterceptor:Interceptor {
    private fun Request.getBodyAsString(): String {
        val requestCopy = this.newBuilder().build()
        val buffer = Buffer()
        requestCopy.body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val response :Response = chain.proceed(original)
        val content:String? =response.body?.string()
        Log.d("upload", original.url.toString())
        val jsonType = "application/json".toMediaTypeOrNull()
        if (original.body?.contentType() == jsonType) {
            Log.d("upload", original.getBodyAsString())
        }
        //Timber.tag("API请求").d("Response Content Type：%s\n\r",response.body?.contentType()?.subtype)
        if (response.body?.contentType()?.subtype == "json") {
            if (content != null) {
                Log.d("upload",content)
            }
        }

        return chain.proceed(original)
    }
}