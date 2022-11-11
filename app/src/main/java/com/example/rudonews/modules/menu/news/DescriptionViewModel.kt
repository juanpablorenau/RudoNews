package com.example.rudonews.modules.menu.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.Pager
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.Comment
import com.example.rudonews.data.model.News
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DescriptionViewModel : ViewModel() {

    val news = MutableLiveData<News>()
    val comment = MutableLiveData<String>("")
    val pager = MutableLiveData<Pager<Comment>>()
    val isLoading = MutableLiveData<Boolean>(false)

    val commentList = MutableLiveData<MutableList<Comment>>(mutableListOf())

    val eventGetCommentsByPostSuccessful = MutableLiveData<Boolean>()
    val eventCreateCommentSuccessful = MutableLiveData<Boolean>()
    val eventSaveFavoritePostSuccessful = MutableLiveData<Boolean>()
    val eventDeleteFavoritePostSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun getCommentsByPost(id: String, page: Int) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getCommentsByPost(id, page)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<Comment>
                                pager.value = body
                                body?.results?.let {
                                    commentList.value?.addAll(it)
                                    commentList.value = commentList.value
                                    eventGetCommentsByPostSuccessful.value = true
                                }
                            } else {
                                eventGetCommentsByPostSuccessful.value = false
                            }
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }

    fun createComment(id: String, comment: Comment) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().createComment(id, comment)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val newComment = response.body() as? Comment
                                newComment?.user = App.user
                                newComment?.let { commentList.value?.add(it) }
                                eventCreateCommentSuccessful.value = true
                            } else {
                                eventCreateCommentSuccessful.value = false
                            }
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }

    fun saveFavoritePost(savePost: HashMap<String, String>) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().saveFavoritePost(savePost)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            eventSaveFavoritePostSuccessful.value = response.isSuccessful
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }

    fun deleteFavoritePost(id: String) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().deleteFavoritePost(id)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            eventDeleteFavoritePostSuccessful.value = response.isSuccessful
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }
}
