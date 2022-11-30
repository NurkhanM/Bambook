package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorSignUp {

    @SerializedName("phone")
    @Expose
    var error: ArrayList<String>? = null
}