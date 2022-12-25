package kg.foodbambook.bambook.model

import kg.foodbambook.kg.R

data class SubcategoryItem(
    val image: Int,

    val name: String,
    val price: Double,
    val freeSideDish: Boolean,
    var amount: Int
)
