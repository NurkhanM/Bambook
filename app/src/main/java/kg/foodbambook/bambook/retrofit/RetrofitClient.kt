package kg.foodbambook.bambook.retrofit

import android.content.Context
import kg.foodbambook.bambook.SaveUserToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var retrofit: Retrofit? = null

    private var token: String? = null

    fun create(context: Context, baseUrl: String): Retrofit? {
        token = SaveUserToken.getToken(context)
        if (retrofit == null) {
            val builder: Retrofit.Builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient(context))
                .addConverterFactory(GsonConverterFactory.create())
            retrofit = builder.build()
        }


        return retrofit
    }

    fun resetRetrofit(){
        retrofit = null
    }
    private fun getClient(context: Context): OkHttpClient {
        val builder =  OkHttpClient()
            .newBuilder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)


        if (SaveUserToken.hasToken(context)){
            builder.addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", SaveUserToken.getToken(context)!!)
                    .build()
                return@addInterceptor chain.proceed(request)
            }
        }
        return builder.build()
    }
}