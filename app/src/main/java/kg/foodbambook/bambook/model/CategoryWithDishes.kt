package kg.foodbambook.bambook.model

data class CategoryWithDishes(
    val id: Long,
    val name: String,
    val dishes: List<Dish>,
    val image: String,
    val slug: String
)