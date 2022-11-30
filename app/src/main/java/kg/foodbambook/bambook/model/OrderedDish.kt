package kg.foodbambook.bambook.model

data class OrderedDish(
    val dish: Int,
    val garnish: Int?,
    val additive: Int?,
    val quantity: Int
)