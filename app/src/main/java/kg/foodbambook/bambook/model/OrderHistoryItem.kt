package kg.foodbambook.bambook.model

data class OrderHistoryItem(
    val elements: ArrayList<DishHistory>,
    val pay_method: PayMethod,
    val first_name: String,
    val phone: String,
    val address: String,
    val building: String,
    val flat: String?,
    val entrance: String?,
    val floor: String?,
    val comment: String?,
    val bonuses: Int?,
    val change: Int?,
    val cutlery: Int?,
    val order_status: String,
    val total: String,
    val created_at: String
)
