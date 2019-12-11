package com.example.biker112.ui.apiManager.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetHistoryRes {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public class Datum {

        @SerializedName("TripDetails")
        @Expose
        private TripDetails tripDetails;

        public TripDetails getTripDetails() {
            return tripDetails;
        }

        public void setTripDetails(TripDetails tripDetails) {
            this.tripDetails = tripDetails;
        }

    }


    public class TripDetails {

        @SerializedName("user_vehicle_id")
        @Expose
        private Integer userVehicleId;
        @SerializedName("start_time")
        @Expose
        private String startTime;
        @SerializedName("trip_status")
        @Expose
        private String tripStatus;
        @SerializedName("distance")
        @Expose
        private Integer distance;
        @SerializedName("duration")
        @Expose
        private Integer duration;
        @SerializedName("avg_speed")
        @Expose
        private Integer avgSpeed;

        public Integer getUserVehicleId() {
            return userVehicleId;
        }

        public void setUserVehicleId(Integer userVehicleId) {
            this.userVehicleId = userVehicleId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTripStatus() {
            return tripStatus;
        }

        public void setTripStatus(String tripStatus) {
            this.tripStatus = tripStatus;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Integer getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(Integer avgSpeed) {
            this.avgSpeed = avgSpeed;
        }


    }
}
