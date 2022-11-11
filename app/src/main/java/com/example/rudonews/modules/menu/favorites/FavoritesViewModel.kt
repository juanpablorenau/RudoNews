package com.example.rudonews.modules.menu.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.Pager
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.News
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FavoritesViewModel : ViewModel() {

    val pager = MutableLiveData<Pager<News>>()
    val isLoading = MutableLiveData<Boolean>(false)

    val favoritesList = MutableLiveData<MutableList<News>>(mutableListOf())

    val eventGetFavoritePostsSuccessful = MutableLiveData<Boolean>()
    val eventSaveFavoritePostSuccessful = MutableLiveData<Boolean>()
    val eventDeleteFavoritePostSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun getFavoritePosts(page: Int) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getFavoritePosts(page)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<News>
                                pager.value = body
                                body?.results?.let {
                                    favoritesList.value?.addAll(it)
                                    favoritesList.value = favoritesList.value
                                    eventGetFavoritePostsSuccessful.value = true
                                }
                            } else {
                                eventGetFavoritePostsSuccessful.value = false
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
