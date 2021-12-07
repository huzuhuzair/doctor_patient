package com.aamir.icarepro.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebew.deliveryagent.data.models.directions.Direction
import com.aamir.icarepro.data.AppDataManager
import com.aamir.icarepro.data.models.ErrorModel
import com.aamir.icarepro.data.models.Resource
import com.aamir.icarepro.data.models.carwash.CarWash
import com.aamir.icarepro.data.models.carwash.ServiceDetails
import com.aamir.icarepro.data.models.chat.ChatMessage
import com.aamir.icarepro.data.models.chat.Conversation
import com.aamir.icarepro.data.models.clients.Client
import com.aamir.icarepro.data.models.clients.ClientsResponse
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.data.models.service.HomeResponse
import com.aamir.icarepro.data.models.service.Insurance
import com.aamir.icarepro.data.models.service.carRepair.CarRepairBooking
import com.aamir.icarepro.utils.SingleLiveEvent
import com.aamir.smartcarservice.data.models.notifications.Notify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*


@ExperimentalCoroutinesApi
class HomeViewModel
@ViewModelInject
constructor(private val appDataManager: AppDataManager) : ViewModel() {


    val docs by lazy { SingleLiveEvent<ArrayList<LoginResponse>>() }
    val conversations by lazy { SingleLiveEvent<ArrayList<Conversation>>() }
    val chats by lazy { SingleLiveEvent<ArrayList<ChatMessage>>() }
    val user by lazy { SingleLiveEvent<Conversation>() }
    val update by lazy { SingleLiveEvent<LoginResponse>() }
    val homeData by lazy { SingleLiveEvent<HomeResponse>() }
    val clients by lazy { SingleLiveEvent<ClientsResponse>() }
    val client by lazy { SingleLiveEvent<Client>() }
    val repairBookings by lazy { SingleLiveEvent<ArrayList<CarRepairBooking>>() }
    val repairBooking by lazy { SingleLiveEvent<CarRepairBooking>() }
    val carWash by lazy { SingleLiveEvent<ArrayList<CarWash>>() }
    val service by lazy { SingleLiveEvent<ArrayList<CarWash>>() }
    val singleService by lazy { SingleLiveEvent<CarWash>() }
    val carWashAddon by lazy { SingleLiveEvent<ArrayList<ServiceDetails>>() }
    val singleCarWash by lazy { SingleLiveEvent<CarWash>() }
    val singleInsurance by lazy { SingleLiveEvent<Insurance>() }
    val insurance by lazy { SingleLiveEvent<ArrayList<Insurance>>() }
    val submitBooking by lazy { SingleLiveEvent<Any>() }
    val loading by lazy { SingleLiveEvent<Boolean>() }
    val direction by lazy { SingleLiveEvent<Direction>() }
    val notifications by lazy { SingleLiveEvent<ArrayList<Notify>>() }

    //
    val error by lazy { SingleLiveEvent<ErrorModel>() }

    fun update(hashMap: HashMap<String, RequestBody>, imageBody: MultipartBody.Part) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.update(hashMap,imageBody).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        update.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun update(hashMap: HashMap<String, RequestBody>) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.update(hashMap).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        update.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getClient(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getClient(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        client.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun getHomeData() {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getHomeData().onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        homeData.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getDoctors(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getDoctors(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        docs.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun getConversations(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getConversations(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        conversations.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getChat(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getChat(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        chats.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    private fun <T> handleError(dataState: Resource.Error<T>) {
        error.value = ErrorModel(
            message = dataState.msg,
            status = dataState.status?.name
        )

    }

    fun loadUserData(id: Int, userId: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getUser(id,userId).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        user.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }




//


}