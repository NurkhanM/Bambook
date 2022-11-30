package kg.foodbambook.bambook.model

import com.google.gson.annotations.SerializedName

data class RestoredPasswordRequestBody(

    @SerializedName("new_password1")
    val password1: String,
    @SerializedName("new_password2")
    val password2: String,
    @SerializedName("phone")
    val phoneNumber: String
)