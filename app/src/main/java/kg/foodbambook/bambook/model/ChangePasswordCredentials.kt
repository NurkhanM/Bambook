package kg.foodbambook.bambook.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordCredentials(

    @SerializedName("old_password")
    val oldPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)