package kg.foodbambook.bambook.model

data class DishHistory(
    val dish: Dish,
    val garnish: Garnish?,
    val additive: Garnish?,
    val quantity: Int
)