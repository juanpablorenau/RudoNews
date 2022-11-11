package com.example.rudonews.api

import com.example.rudonews.App
import com.example.rudonews.BuildConfig
import com.example.rudonews.data.model.*
import com.example.rudonews.data.model.calls.LoginRequest
import com.example.rudonews.data.model.calls.RegisterRequest
import com.example.rudonews.data.model.calls.UpdateProfileRequest
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException

class RetrofitClient {

    enum class ErrorType {
        HTTP_EXCEPTION, // HTTP
        NETWORK, // IO
        TIMEOUT, // Socket
        UNKNOWN // Anything else
    }

    interface RemoteEmitter {
        fun onResponse(response: Response<Any>)
        fun onError(errorType: ErrorType, msg: String)
    }

    private val clientWithoutAuth by lazy {
        Retrofit.Builder().baseUrl(Config.API_URL).client(
            OkHttpClient().newBuilder().addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BODY)
                    .request("Request")
                    .response("Response")
                    .addHeader("Content-Type", "application/json")
                    .build()
            ).build()
        )
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Api::class.java)
    }

    private val clientWithAuth by lazy {
        Retrofit.Builder().baseUrl(Config.API_URL)
            .client(
                OkHttpClient().newBuilder()
                    .addInterceptor(AccessTokenInterceptor())
                    .addInterceptor(
                        LoggingInterceptor.Builder()
                            .loggable(BuildConfig.DEBUG)
                            .setLevel(Level.BODY)
                            .request("Request")
                            .response("Response")
                            .addHeader("Content-Type", "application/json").build()
                    )
                    .authenticator(AccessTokenAuthenticator())
                    .build()
            ).addConverterFactory(GsonConverterFactory.create()).build().create(Api::class.java)
    }

    fun refreshToken(): Call<Token> {
        val loginRequest = LoginRequest()

        loginRequest.refresh_token = App.preferences.getRefreshToken()
        loginRequest.grant_type = Config.GRANT_TYPE

        return clientWithoutAuth.refreshToken(loginRequest)
    }

    suspend inline fun <T> apiCall(
        crossinline responseFunction: suspend () -> T,
        emitter: RemoteEmitter
    ) {
        try {
            val response = withContext(Dispatchers.IO) { responseFunction.invoke() }
            withContext(Dispatchers.Main) {
                emitter.onResponse(response as Response<Any>)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                when (e) {
                    is HttpException -> {
                        val body = e.response()?.errorBody().toString()
                        emitter.onError(ErrorType.HTTP_EXCEPTION, body)
                    }
                    is SocketTimeoutException -> emitter.onError(
                        ErrorType.TIMEOUT,
                        "Timeout Error"
                    )
                    is IOException -> emitter.onError(ErrorType.NETWORK, "Thread Error")
                    else -> emitter.onError(ErrorType.UNKNOWN, "Unknown Error")
                }
            }
        }
    }

    // AUTH
    suspend fun logIn(loginRequest: LoginRequest) = clientWithoutAuth.logIn(loginRequest)
    suspend fun register(registerRequest: RegisterRequest) = clientWithoutAuth.register(registerRequest)

    // USERS
    suspend fun getLoggedUser() = clientWithAuth.getLoggedUser()
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) = clientWithAuth.updateProfile(updateProfileRequest)
    suspend fun changePassword(changePassword: ChangePassword) = clientWithAuth.changePassword(changePassword)
    suspend fun resetPassword(email: HashMap<String, String>) = clientWithoutAuth.resetPassword(email)

    // CATEGORIES
    suspend fun getCategories() = clientWithoutAuth.getCategories()

    // POSTS
    suspend fun getPosts(page: Int) = clientWithoutAuth.getPosts(page)
    suspend fun saveFavoritePost(savePost: HashMap<String, String>) = clientWithAuth.saveFavoritePost(savePost)
    suspend fun deleteFavoritePost(id: String) = clientWithAuth.deleteFavoritePost(id)
    suspend fun getFavoritePosts(page: Int) = clientWithAuth.getFavoritePosts(page)
    suspend fun getPostsByCategory(categories: MutableList<String>, page: Int) = clientWithoutAuth.getPostsByCategory(categories, page)
    suspend fun getPostsBySearch(text: String) = clientWithoutAuth.getPostsBySearch(text)

    // COMMENTS
    suspend fun getCommentsByPost(id: String, page: Int) = clientWithAuth.getCommentsByPost(id, page)
    suspend fun createComment(id: String, comment: Comment) = clientWithAuth.createComment(id, comment)

    // DEPARTMENTS
    suspend fun getDepartments(page: Int) = clientWithoutAuth.getDepartments(page)
}
