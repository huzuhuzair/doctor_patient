package com.aamir.icarepro.data.network

import com.codebew.deliveryagent.data.models.directions.Direction
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
import com.aamir.smartcarservice.data.models.notifications.Notify
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST(NetworkConstants.LOGIN)
    suspend fun login(@FieldMap hashMap: HashMap<String, String>): Response<ApiResponse<LoginResponse>>

    @Multipart
    @POST(NetworkConstants.UPDATE)
    suspend fun update(@PartMap hashMap: HashMap<String, RequestBody>): Response<ApiResponse<LoginResponse>>

    @Multipart
    @POST(NetworkConstants.REGISTER)
    suspend fun register(@PartMap hashMap: HashMap<String, RequestBody>,@Part image: MultipartBody.Part? ): Response<ApiResponse<Any>>

    @Multipart
    @POST(NetworkConstants.UPDATE)
    suspend fun update(@PartMap hashMap: HashMap<String, RequestBody>,@Part image: MultipartBody.Part? ): Response<ApiResponse<LoginResponse>>


    @GET(NetworkConstants.DOCTORS + "/{id}")
    suspend fun getDoctors(@Path("id") id: Int): Response<ApiResponse<ArrayList<LoginResponse>>>

    @GET(NetworkConstants.CONVERSATIONS + "/{id}")
    suspend fun getConversations(@Path("id") id: Int): Response<ApiResponse<ArrayList<Conversation>>>

    @GET(NetworkConstants.CHATS + "/{id}")
    suspend fun getChat(@Path("id") id: Int): Response<ApiResponse<ArrayList<ChatMessage>>>

    @GET(NetworkConstants.USER + "/{id}")
    suspend fun getUser(
        @Path("id") id: Int,
        @Query("user_id") userId: Int
    ): Response<ApiResponse<Conversation>>

    @GET(NetworkConstants.HOME)
    suspend fun getHomeData(): Response<ApiResponse<HomeResponse>>

    @GET(NetworkConstants.CLIENTS)
    suspend fun getClients(): Response<ApiResponse<ClientsResponse>>

    @GET(NetworkConstants.CLIENT + "/{user_id}")
    suspend fun getClient(@Path("user_id") id: Int): Response<ApiResponse<Client>>

    @GET(NetworkConstants.REPAIRS + "/{status}")
    suspend fun getRepairBookings(@Path("status") status: Int): Response<ApiResponse<ArrayList<CarRepairBooking>>>

    @GET(NetworkConstants.CAR_WASH)
    suspend fun carWash(): Response<ApiResponse<ArrayList<CarWash>>>

    @GET(NetworkConstants.CAR_WASH_ADDON + "/{service_sub_types_id}")
    suspend fun addOnCarWash(@Path("service_sub_types_id") id: Int): Response<ApiResponse<ArrayList<ServiceDetails>>>

    @GET(NetworkConstants.CAR_WASH + "/{car_wash_id}")
    suspend fun singleCarWash(@Path("car_wash_id") id: Int): Response<ApiResponse<CarWash>>

    @GET(NetworkConstants.CAR_INSURANCE + "/{car_insurance_id}")
    suspend fun getSingleInsurance(@Path("car_insurance_id") id: Int): Response<ApiResponse<Insurance>>

    @GET(NetworkConstants.SERVICE + "/{services_maintanace_id}")
    suspend fun getSingleService(@Path("services_maintanace_id") id: Int): Response<ApiResponse<CarWash>>

    @GET(NetworkConstants.CAR_INSURANCE_COMPLETE + "/{car_insurance_id}")
    suspend fun completeInsurance(@Path("car_insurance_id") id: Int): Response<ApiResponse<Insurance>>

    @GET(NetworkConstants.CAR_SERVICE_COMPLETE + "/{services_maintanace_id}")
    suspend fun completeService(@Path("services_maintanace_id") id: Int): Response<ApiResponse<CarWash>>

    @GET(NetworkConstants.CAR_WASH + "/{car_wash_id}/{status}")
    suspend fun changeStatusCarWash(
        @Path("car_wash_id") id: Int,
        @Path("status") status: Int
    ): Response<ApiResponse<CarWash>>

    @GET(NetworkConstants.SERVICE_AND_MAINT + "/{status}")
    suspend fun serviceAndMaintenance(@Path("status") status: Int): Response<ApiResponse<ArrayList<CarWash>>>

    @GET(NetworkConstants.CAR_INSURANCE)
    suspend fun getInsurance(): Response<ApiResponse<ArrayList<Insurance>>>

    @GET(NetworkConstants.REPAIR_STATUS + "/{car_repaires_id}")
    suspend fun changeStatusCarRepair(
        @Path("car_repaires_id") carRepairesId: Int,
        @Query("status") status: Int
    ): Response<ApiResponse<CarRepairBooking>>

    @GET(NetworkConstants.DIRECTION_API)
    suspend fun getDirection(
        @Query("origin") date: String,
        @Query("destination") page: String,
        @Query("mode") s: String,
        @Query("key") s1: String,
        @Query("sensor") b: Boolean
    ): Response<Direction>

    @GET(NetworkConstants.NOTIFICATIONS)
    suspend fun getNotifications(): Response<ApiResponse<ArrayList<Notify>>>
}