package com.example.nasa_googlemaps_api_project.home.network

import com.example.nasa_googlemaps_api_project.home.HomeGlobals.API_KEY
import com.example.nasa_googlemaps_api_project.home.HomeGlobals.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ImageOfDayApiClient {

    companion object {

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain -> return@addInterceptor  addApiKeyToClient(chain)}
            .build()

        /** Creates the api for for the Nasa Image Of The Day **/
        fun createApi() : ImageOfDayApiInterface {

            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(ImageOfDayApiInterface::class.java)
        }

        /** Adds the Nasa Api key to the Http client**/
        private fun addApiKeyToClient(chain: Interceptor.Chain) : Response {
            val request = chain.request().newBuilder()
            val originalUrl = chain.request().url()
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", API_KEY).build()
            request.url(newUrl)
            return chain.proceed(request.build())
        }
    }
}