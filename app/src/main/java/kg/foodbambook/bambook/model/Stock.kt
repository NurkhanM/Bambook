package kg.foodbambook.bambook.model

data class Stock(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val is_active: Boolean,
    val end_date: String
)