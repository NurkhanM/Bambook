package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDto (
    @Expose
    @SerializedName("first_name")
    val firstName: String,
    val address: String?,
    val building: String?,
    val flat: String?
)
