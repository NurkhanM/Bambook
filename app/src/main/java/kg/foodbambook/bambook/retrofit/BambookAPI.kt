package kg.foodbambook.bambook.retrofit


import kg.foodbambook.bambook.model.*
import kg.foodbambook.bambook.model.version.Version
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BambookAPI {

    @Multipart
    @POST("/api/v1/accounts/signup/")
    fun signUp(
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody
    ): Call<Token>

    @Multipart
    @POST("/api/v1/accounts/login/")
    fun login(
        @Part("username") phone: RequestBody,
        @Part("password") password: RequestBody
    ): Call<Token>

    @GET("/api/v1/categories/")
    fun getCategories(): Call<List<Category>>

    @GET("/api/v1/dishes/")
    fun getDishes(@Query("category") id: Long): Call<List<Dish>>

    @GET("/api/v1/dishes/")
    fun searchDishes(
        @Query("search") searchWrd: String = "",
        @Query("category") id: String = ""
    ): Call<List<Dish>>

    @GET("/api/v1/dishes/{id}/")
    fun getDish(@Path("id") id: Int): Call<Dish>

    @POST("/api/v1/orders/")
    fun createOrder(@Body order: Order): Call<OrderGet>

    @GET("/api/v1/stocks/")
    fun getStocks(): Call<ArrayList<Stock>>

    @GET("/api/v1/contacts/")
    fun getContacts(): Call<Contacts>

    @GET("/api/v1/sliders/")
    fun getSliderImages(): Call<ArrayList<SliderImage>>

    @GET("/api/v1/accounts/check/{phone}/")
    fun checkNumber(@Path("phone") phone: String): Call<NumberCheck>

    @PUT("/api/v1/accounts/reset_password/")
    fun restorePassword(
        @Body newCredentials: RestoredPasswordRequestBody
    ): Call<Any>


    @PUT("/api/v1/accounts/change_password/")
    fun changePassword(
        @Body passwordCredentials: ChangePasswordCredentials
    ): Call<Any>

    @GET("/api/v1/accounts/profile/")
    fun getProfileInfo(): Call<User>

    @PUT("/api/v1/accounts/profile/")
    fun updateProfile(@Body user: UserDto): Call<User>

    @GET("/api/v1/orders/")
    fun getOrderHistory(): Call<List<OrderHistoryItem>>

    @GET("/api/v1/version/")
    fun getVersion(): Call<Version>

    @GET("/api/v1/active_popup_stock/")
    fun getPromotion(): Call<Promotion>

    @GET("/api/v1/check_worktime")
    fun checkWorkTime(): Call<CheckWorkTimeResponse>
}