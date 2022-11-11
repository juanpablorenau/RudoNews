package com.example.rudonews.modules.menu.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.Pager
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.Category
import com.example.rudonews.data.model.News
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel : ViewModel() {

    val pager = MutableLiveData<Pager<News>>()
    val isLoading = MutableLiveData<Boolean>(false)

    val newsList = MutableLiveData<MutableList<News>>(mutableListOf())
    val categoriesList = MutableLiveData<MutableList<Category>>()
    val selectedCategoriesList = MutableLiveData<MutableList<String>>(mutableListOf())

    val eventGetCategoriesSuccessful = MutableLiveData<Boolean>()
    val eventGetNewsSuccessful = MutableLiveData<Boolean>()
    val eventSaveFavoritePostSuccessful = MutableLiveData<Boolean>()
    val eventDeleteFavoritePostSuccessful = MutableLiveData<Boolean>()
    val eventGetPostsByCategorySuccessful = MutableLiveData<Boolean>()
    val eventGetPostsBySearchSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun getCategories() {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getCategories()
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<Category>
                                categoriesList.value = body?.results
                                eventGetCategoriesSuccessful.value = true
                            } else {
                                eventGetCategoriesSuccessful.value = false
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

    fun getNews(page: Int) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getPosts(page)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<News>
                                pager.value = body
                                body?.results?.let {
                                    newsList.value?.addAll(it)
                                    newsList.value = newsList.value
                                    eventGetNewsSuccessful.value = true
                                }
                            } else {
                                eventGetNewsSuccessful.value = true
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

    fun getPostsByCategory(categories: MutableList<String>, page: Int) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getPostsByCategory(categories, page)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<News>
                                pager.value = body
                                newsList.value = body?.results
                                eventGetPostsByCategorySuccessful.value = true
                            } else {
                                eventGetPostsByCategorySuccessful.value = false
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

    fun getPostsBySearch(text: String) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getPostsBySearch(text)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<News>
                                pager.value = body
                                newsList.value = body?.results
                                eventGetPostsBySearchSuccessful.value = true
                            } else {
                                eventGetPostsBySearchSuccessful.value = false
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
}
