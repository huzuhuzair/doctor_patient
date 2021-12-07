package com.aamir.icarepro.data

import androidx.datastore.preferences.Preferences
import com.codebew.deliveryagent.data.models.directions.Direction
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.*
import com.aamir.icarepro.data.models.carwash.CarWash
import com.aamir.icarepro.data.models.carwash.ServiceDetails
import com.aamir.icarepro.data.models.chat.ChatMessage
import com.aamir.icarepro.data.models.chat.Conversation
import com.aamir.icarepro.data.models.clients.Client
import com.aamir.icarepro.data.models.clients.ClientsResponse
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.data.models.service.HomeResponse
import com.aamir.icarepro.data.models.service.Insurance
import com.aamir.icarepro.data.models.service.ServiceData
import com.aamir.icarepro.data.models.service.carRepair.CarRepairBooking
import com.aamir.icarepro.data.network.ApiService
import com.aamir.icarepro.utils.apiRequest
import com.aamir.icarepro.utils.apiRequestDirection
import com.aamir.smartcarservice.data.models.notifications.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class AppDataManager
@Inject
constructor(
    private val mDataStoreHelper: DataStoreHelper,
    private val mApiHelper: ApiService
) : DataManager {
    override suspend fun setUserAsLoggedOut() {
        mDataStoreHelper.logOut()
    }

    override fun updateUserInf(): HashMap<String, String> {
        return HashMap<String, String>()
    }

    override fun updateApiHeader(userId: Long?, accessToken: String) {

    }

    override suspend fun <T> setKeyValue(key: Preferences.Key<T>, value: T) {
        mDataStoreHelper.setKeyValue(key, value)

    }

    override fun <T> getKeyValue(key: Preferences.Key<T>): Flow<Any?> {
        return mDataStoreHelper.getKeyValue(key)
    }

    override suspend fun <T> getGsonValue(key: Preferences.Key<String>, type: Class<T>): Flow<T?> {
        return mDataStoreHelper.getGsonValue(key, type)

    }

    override suspend fun addGsonValue(key: Preferences.Key<String>, value: String) {
        return mDataStoreHelper.addGsonValue(key, value)

    }


    override suspend fun logOut() {
        mDataStoreHelper.logOut()
    }

    override suspend fun clear() {
        mDataStoreHelper.clear()
    }

    override suspend fun getCurrentUserLoggedIn(): Flow<Boolean> {
        return mDataStoreHelper.getCurrentUserLoggedIn()
    }

    override suspend fun isDocumentUploaded(): Flow<Boolean> {
        return mDataStoreHelper.isDocumentUploaded()

    }

    suspend fun login(hashMap: HashMap<String, String>): Flow<Resource<LoginResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.login(hashMap)
            }
        emit(data)

    }
    suspend fun register(hashMap: java.util.HashMap<String, RequestBody>, imageBody: MultipartBody.Part): Flow<Resource<Any>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.register(hashMap,imageBody)
            }
        emit(data)

    }
    suspend fun update(hashMap: java.util.HashMap<String, RequestBody>, imageBody: MultipartBody.Part): Flow<Resource<LoginResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.update(hashMap,imageBody)
            }
        emit(data)

    }
    suspend fun update(hashMap: java.util.HashMap<String, RequestBody>): Flow<Resource<LoginResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.update(hashMap)
            }
        emit(data)

    }
    suspend fun getHomeData(): Flow<Resource<HomeResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getHomeData()
            }
        emit(data)

    }

    suspend fun getDoctors(id: Int): Flow<Resource<ArrayList<LoginResponse>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getDoctors(id)
            }
        emit(data)

    }
    suspend fun getConversations(id: Int): Flow<Resource<ArrayList<Conversation>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getConversations(id)
            }
        emit(data)

    }
    suspend fun getChat(id: Int): Flow<Resource<ArrayList<ChatMessage>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getChat(id)
            }
        emit(data)

    }
    suspend fun getUser(id: Int,userId:Int): Flow<Resource<Conversation>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getUser(id,userId)
            }
        emit(data)

    }



    suspend fun getClient(id: Int): Flow<Resource<Client>?> = flow {
        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getClient(id)
            }
        emit(data)

    }
}