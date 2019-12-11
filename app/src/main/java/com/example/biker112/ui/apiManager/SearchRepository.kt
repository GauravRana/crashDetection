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
import io.reactivex.Observable

/**
 * Repository method to access search functionality of the api service
 */
class SearchRepository(val apiService: ApiService) {

    fun userOtp(jsonObject: JsonObject): io.reactivex.Observable<SendOTPResponse> {
        return apiService.sendOTP(jsonObject)
    }


    fun verifyOTP(jsonObject: JsonObject): io.reactivex.Observable<VerifyOTPRes> {
        return apiService.verifyOTP(jsonObject)
    }


    fun getProfile(auth: String, userID: String): io.reactivex.Observable<GetProfileRes> {
        return apiService.getProfile(auth, userID)
    }

    fun getEmergencyContact(auth: String, userID: String): io.reactivex.Observable<GetEmergencyContact> {
        return apiService.getEmergencyContacts(auth, userID)
    }

    fun addEmergencyContact(auth: String, jsonObject: JsonObject): io.reactivex.Observable<AddEmergencyContact> {
        return apiService.addEmergencyContacts(auth, jsonObject)
    }


    fun updatePreference(auth: String, jsonObject: JsonObject): io.reactivex.Observable<UpdatePrefRes> {
        return apiService.updatePreference(auth, jsonObject)
    }

    fun deleteEmergencyContact(auth: String, id: String): io.reactivex.Observable<DeleteEmergencyContRes> {
        return apiService.deleteEmergencyContact(auth, id)
    }


    fun getVehicle(auth: String,  userID: String): io.reactivex.Observable<GetVehicleResponse> {
        return apiService.getVehicle(auth, userID)
    }


    fun addVehicle(auth: String, jsonObject: JsonObject): io.reactivex.Observable<AddVehicleRes> {
        return apiService.addVehicle(auth, jsonObject)
    }

    fun deleteVehicle(auth: String, jsonObject: JsonObject): io.reactivex.Observable<GetVehicleResponse> {
        return apiService.deleteVehicle(auth, jsonObject)
    }

    fun rideHistory(auth: String, userID: String): io.reactivex.Observable<GetHistoryRes> {
        return apiService.getTripHistory(auth, userID)
    }


    fun postProfile(auth: String, jsonObject: JsonObject, userID: String): io.reactivex.Observable<GetProfileRes> {
        return apiService.postProfile(auth, jsonObject, userID)
    }


    fun startTrip(auth: String, jsonObject: JsonObject): io.reactivex.Observable<StartTripRes> {
        return apiService.startTrip(auth, jsonObject)
    }



    fun submitCrashResponse(auth: String, jsonObject: JsonObject): io.reactivex.Observable<CrashRes> {
        return apiService.submitCrashResponse(auth, jsonObject)
    }


    fun addRoutes(auth: String, jsonObject: JsonObject): io.reactivex.Observable<CrashRes> {
        return apiService.addRoutes(auth, jsonObject)
    }




}