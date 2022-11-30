package kg.foodbambook.bambook.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody

object RetrofitErrorUtils {
        fun <T> parseError(errorBody: ResponseBody, classOfT :Class<T>): T{
            val builder = GsonBuilder()
            val gson: Gson = builder.create()

            return gson.fromJson<T>(errorBody.string(), classOfT)
        }


}