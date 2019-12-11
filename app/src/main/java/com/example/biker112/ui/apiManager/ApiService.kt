package com.example.biker112.ui.apiManager

import com.example.biker112.ui.apiManager.emergency.AddEmergencyContact
import com.example.biker112.ui.apiManager.emergency.DeleteEmergencyContRes
import com.example.biker112.ui.apiManager.emergency.GetEmergencyContact
import com.example.biker112.ui.apiManager.emergency.UpdatePrefRes
import com.example.biker112.ui.apiManager.history.GetHistoryRes
import com.example.biker112.ui.apiManager.login.SendOTPResponse
import com.example.biker112.ui.apiManager.login.VerifyOTPRes
import com.example.biker112.ui.apiManager.profile.GetProfileRes
import com.example.biker112.ui.apiManager.startTrip.CrashRes
import com.example.biker112.ui.apiManager.startTrip.StartTripRes
import com.example.biker112.ui.apiManager.vehicle.AddVehicleRes
import com.example.biker112.ui.apiManager.vehicle.GetVehicleResponse
import com.google.gson.JsonObject

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*


interface ApiService {
    /**
     * Companion object for the factory
     */
    companion object Factory {
        fun createMain(url: String): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .client(client)
                .baseUrl(url)  // https://reqres.in/
                .build()

            return retrofit.create(ApiService::class.java);
        }




        fun createOther(url: String): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .client(client)
                .baseUrl(url)  // https://reqres.in/
                .build()

            return retrofit.create(ApiService::class.java);
        }



    }
    //https://api.github.com/

    @retrofit2.http.POST("mountainbikerapi/Otps/sendOTP?XDEBUG_SESSION_START=netbeans-xdebug")
    fun sendOTP(
        @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<SendOTPResponse>



    @retrofit2.http.POST("mountainbikerapi/users/verifyLogin")
    fun verifyOTP(
        @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<VerifyOTPRes>

    @retrofit2.http.GET("mountainbikerapi/users/view/{userID}.json")
    fun getProfile(@Header("Authorization") authorisation: String, @Path(value = "userID")  id: String
    ): io.reactivex.Observable<GetProfileRes>


    @retrofit2.http.PUT("mountainbikerapi/users/edit/{userID}.json?XDEBUG_SESSION_START=netbeans-xdebug")
    fun postProfile(@Header("Authorization") authorisation: String ,  @retrofit2.http.Body jsonObject: JsonObject,@Path(value = "userID")  id: String
    ): io.reactivex.Observable<GetProfileRes>



    @retrofit2.http.GET("mountainbikerapi/EmergencyContacts/getContactByUserID/{userID}?XDEBUG_SESSION_START=netbeans-xdebug")
    fun getEmergencyContacts(@Header("Authorization") authorisation: String, @Path(value = "userID")  id: String
    ): io.reactivex.Observable<GetEmergencyContact>


    @retrofit2.http.POST("mountainbikerapi/EmergencyContacts/add.json?XDEBUG_SESSION_START=netbeans-xdebug")
    fun addEmergencyContacts(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<AddEmergencyContact>

    @retrofit2.http.POST("mountainbikerapi/EmergencyContacts/updatePreference?XDEBUG_SESSION_START=netbeans-xdebug")
    fun updatePreference(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<UpdatePrefRes>


    @retrofit2.http.DELETE("mountainbikerapi/emergency-contacts/delete/{id}.json?XDEBUG_SESSION_START=netbeans-xdebug")
    fun deleteEmergencyContact(@Header("Authorization") authorisation: String,  @Path(value = "id")  id: String
    ): io.reactivex.Observable<DeleteEmergencyContRes>



    @retrofit2.http.GET("/mountainbikerapi/vehicles/getVehiclesByUserID/{userID}?XDEBUG_SESSION_START=netbeans-xdebug")
    fun getVehicle(@Header("Authorization") authorisation: String, @Path(value = "userID")  id: String
    ): io.reactivex.Observable<GetVehicleResponse>


    @retrofit2.http.POST("/mountainbikerapi/vehicles/add.json?XDEBUG_SESSION_START=netbeans-xdebug")
    fun addVehicle(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<AddVehicleRes>


    @retrofit2.http.POST("mountainbikerapi/vehicles/delete?XDEBUG_SESSION_START=netbeans-xdebug")
    fun deleteVehicle(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<GetVehicleResponse>


    @retrofit2.http.GET("/mountainbikerapi/trips/viewTrip/{userID}")
    fun getTripHistory(@Header("Authorization") authorisation: String, @Path(value = "userID")  stayID: String
    ): io.reactivex.Observable<GetHistoryRes>

    @retrofit2.http.POST("/mountainbikerapi/trips/add.json")
    fun startTrip(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<StartTripRes>


    @retrofit2.http.POST("/mountainbikerapi/trips/activities.json")
    fun submitCrashResponse(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<CrashRes>


    @retrofit2.http.POST("/mountainbikerapi/RoutePoints/add.json")
    fun addRoutes(@Header("Authorization") authorisation: String,  @retrofit2.http.Body jsonObject: JsonObject
    ): io.reactivex.Observable<CrashRes>



}