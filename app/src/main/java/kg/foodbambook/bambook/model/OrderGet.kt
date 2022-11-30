package kg.foodbambook.bambook.model

data class OrderGet(
    var elements: ArrayList<OrderedDish>,
    val pay_method: PayMethod,
    val first_name: String,
    val phone: String,
    val address: String,
    val building: String,
    val flat: String?,
    val entrance: String?,
    val floor: String?,
    val comment: String?,
    val bonuses: Int,
    val change: Int?,
    val cutlery: Int?,
    val firebase_device: String?,
    val future_bonuses: String
    )