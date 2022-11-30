package kg.foodbambook.bambook.model

import com.google.gson.annotations.SerializedName

data class Promotion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String
)
