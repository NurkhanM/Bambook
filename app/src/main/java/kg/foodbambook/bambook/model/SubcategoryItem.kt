package kg.foodbambook.bambook.model

import kg.foodbambook.bambook.R

data class SubcategoryItem(
    val image: Int,

    val name: String,
    val price: Double,
    val freeSideDish: Boolean,
    var amount: Int
)
