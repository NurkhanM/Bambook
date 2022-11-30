package kg.foodbambook.bambook.model

import com.google.gson.annotations.SerializedName

data class CheckWorkTimeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("error")
    val error: String?
)
