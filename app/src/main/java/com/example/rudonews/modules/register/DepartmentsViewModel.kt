package com.example.rudonews.modules.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.AppPreferences
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.Pager
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.Department
import com.example.rudonews.data.model.SharedPrefDepartments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DepartmentsViewModel : ViewModel() {

    private val preferences = App.preferences

    val comesFromEditActivity = MutableLiveData<Boolean>()
    val pager = MutableLiveData<Pager<Department>>()

    val departmentList = MutableLiveData<MutableList<Department>>(mutableListOf())

    val eventDepartmentsSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun getList() {
        val sharedPrefDepartment =
            preferences.getObject(AppPreferences.DEPARTMENT_LIST)
        sharedPrefDepartment?.let {
            departmentList.value = sharedPrefDepartment.deparments.toMutableList()
        } ?: run {
            getDepartments(1)
        }
    }

    fun saveList() {
        departmentList.value?.let { list ->
            preferences.setObject(
                AppPreferences.DEPARTMENT_LIST,
                SharedPrefDepartments(list)
            )
        }
    }

    fun getDepartments(page: Int) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getDepartments(page)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val body = response.body() as? Pager<Department>
                                pager.value = body
                                body?.results?.let { departmentList.value?.addAll(it) }
                                eventDepartmentsSuccessful.value = true
                            } else {
                                eventDepartmentsSuccessful.value = false
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
