package com.aamir.icarepro.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamir.icarepro.data.models.Resource
import com.aamir.icarepro.data.AppDataManager
import com.aamir.icarepro.data.models.ErrorModel
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.utils.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

@ExperimentalCoroutinesApi
class AuthenticationViewModel
@ViewModelInject
    constructor(
    private val appDataManager: AppDataManager

) : ViewModel(){
    val loginData by lazy { SingleLiveEvent<LoginResponse>() }
    val register by lazy { SingleLiveEvent<Any>() }
    val error by lazy { SingleLiveEvent<ErrorModel>() }
    val loading by lazy { SingleLiveEvent<Boolean>() }
    fun login(hashMap: HashMap<String,String>) {
       // setIsLoading(true)
        viewModelScope.launch {
            appDataManager.login(hashMap).onEach { dataState ->
               // setIsLoading(false)
                when (dataState) {
                    is Resource.Success ->
                        loginData.value = dataState.data!!
                    is Resource.Error -> handleError(dataState)
                    else -> {}


                }
            }.launchIn(viewModelScope)
        }
    }
    fun register(hashMap: java.util.HashMap<String, RequestBody>, imageBody: MultipartBody.Part) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.register(hashMap,imageBody).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        register.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    private fun <T> handleError(dataState: Resource.Error<T>) {
        error.value =  ErrorModel(
            message = dataState.msg,
            status = dataState.status?.name
        )

    }


}