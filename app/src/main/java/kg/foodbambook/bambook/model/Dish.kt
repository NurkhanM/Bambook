package kg.foodbambook.bambook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Dish(
    val id: Int,
    var garnishes: ArrayList<Garnish>?,
    var additives: ArrayList<Garnish>?,
    var name: String,
    val photo: String,
    val output: String,
    val price: Double?,
    val description: String?,

    @Expose
    @SerializedName("with_garnish")
    val withGarnish: Boolean,
    val with_additive: Boolean,
    val is_new: Boolean,
    val slug: String?,
    val category: Long?
): Serializable