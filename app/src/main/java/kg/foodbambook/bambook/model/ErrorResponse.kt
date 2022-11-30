package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {

    @SerializedName("non_field_errors")
    @Expose
    var error: ArrayList<String>? = null
}