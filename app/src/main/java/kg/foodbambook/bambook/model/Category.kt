package kg.foodbambook.bambook.model

import kg.foodbambook.bambook.R
import java.io.Serializable

data class Category(
    val id: Long,
    val name: String,
    val image: String,
    val slug: String
): Serializable
