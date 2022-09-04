package com.monquiz.network

import android.content.Intent
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.monquiz.ui.SignUpActivity
import com.monquiz.utils.OwlizApp
import es.dmoral.toasty.Toasty

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ServiceBuilderForLocalHost {

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // Create a Custom Interceptor to apply Headers application wide
    private val headerInterceptor = Interceptor { chain ->
        var request = chain.request()
        Log.i("ApiCall",request.toString())

  /*      try {
            if (DashBoard.token!! == null) {

                Log.i("ServiceBuilder", "FcmToken:Token null")
            } else {
                request = request.newBuilder()
                    //   .addHeader("x-device-type", Build.DEVICE)
                    .addHeader("Authorization", DashBoard.token!!)
                    .build()
            }
        } catch (e: NullPointerException) {
            print("ServicebuilderError:$e")
        }*/

        val response = chain.proceed(request)
        if (response.code == 401) {
            PreferenceManager.getDefaultSharedPreferences(OwlizApp.getCtx()).edit().clear().apply()
            val intent =  Intent(OwlizApp.getCtx(), SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OwlizApp.getCtx()?.let { ContextCompat.startActivity(it, intent, null) }
            Looper.prepare()
            Toasty.error(OwlizApp.getCtx()!!,
                "user unauthorized to access. Please login again to continue",
                Toasty.LENGTH_LONG).show()
            Looper.loop()
            return@Interceptor response
        }else{
            return@Interceptor response
        }
        response
    }

    // Create OkHttp Client
    private val okHttp = OkHttpClient.Builder()
        .callTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60,TimeUnit.SECONDS)
        .writeTimeout(60,TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(logger)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))

    // Create Retrofit Builder
    private val builder = Retrofit.Builder().baseUrl(EndPoints.Base_Url)
        .addConverterFactory(NullOnEmptyConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    // Create Retrofit Instance
    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}