package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @Expose
    @SerializedName("first_name")
    val firstName: String,

    val phone: String?,
    val address: String?,
    val building: String?,
    val flat: String?,
    val password: String?,
    val bonuses: Int?
)