package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("token")
    @Expose
    val token: String

)