package com.example.rudonews.api

import com.example.rudonews.data.model.*
import com.example.rudonews.data.model.calls.LoginRequest
import com.example.rudonews.data.model.calls.RegisterRequest
import com.example.rudonews.data.model.calls.UpdateProfileRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @POST("auth/token/")
    fun refreshToken(@Body loginRequest: LoginRequest): Call<Token>

    // AUTH
    @POST("auth/token/")
    suspend fun logIn(@Body loginRequest: LoginRequest): Response<Token>

    @POST("users/register/")
    suspend fun register(@Body register: RegisterRequest): Response<Token>

    // USERS
    @GET("users/profile/")
    suspend fun getLoggedUser(): Response<User>

    @PUT("users/modify/")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<User>

    @POST("users/change-password/")
    suspend fun changePassword(@Body changePassword: ChangePassword): Response<ChangePassword>

    @POST("users/reset/")
    suspend fun resetPassword(@Body email: HashMap<String, String>): Response<User>

    // CATEGORIES
    @GET("categories/")
    suspend fun getCategories(): Response<Pager<Category>>

    // POSTS
    @GET("posts/")
    suspend fun getPosts(@Query("page") page: Int?): Response<Pager<News>>

    @POST("save-post/")
    suspend fun saveFavoritePost(@Body savePost: HashMap<String, String>): Response<News>

    @DELETE("save-post/{id}/remove/")
    suspend fun deleteFavoritePost(@Path(value = "id") id: String): Response<News>

    @GET("posts/saved/")
    suspend fun getFavoritePosts(@Query("page") page: Int?): Response<Pager<News>>

    @GET("posts/")
    suspend fun getPostsByCategory(
        @Query("categories") categories: MutableList<String>?,
        @Query("page") page: Int?
    ): Response<Pager<News>>

    @GET("posts/")
    suspend fun getPostsBySearch(@Query("text") text: String?): Response<Pager<News>>

    // COMMENTS
    @GET("posts/{id}/comments/")
    suspend fun getCommentsByPost(
        @Path(value = "id") id: String,
        @Query("page") page: Int?
    ): Response<Pager<Comment>>

    @POST("posts/{id}/create_comment/")
    suspend fun createComment(
        @Path(value = "id") id: String,
        @Body comment: Comment
    ): Response<Comment>

    // DEPARTMENTS
    @GET("departments/")
    suspend fun getDepartments(@Query("page") page: Int?): Response<Pager<Department>>
}
