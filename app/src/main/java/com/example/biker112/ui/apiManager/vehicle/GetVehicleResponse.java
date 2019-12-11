package com.example.biker112.ui.apiManager.vehicle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetVehicleResponse {
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

    public class InsurancePolicy {

        @SerializedName("policy_id")
        @Expose
        private Integer policyId;
        @SerializedName("policy_name")
        @Expose
        private String policyName;
        @SerializedName("policy_number")
        @Expose
        private String policyNumber;
        @SerializedName("provider")
        @Expose
        private String provider;
        @SerializedName("start_date")
        @Expose
        private Object startDate;
        @SerializedName("end_date")
        @Expose
        private Object endDate;

        public Integer getPolicyId() {
            return policyId;
        }

        public void setPolicyId(Integer policyId) {
            this.policyId = policyId;
        }

        public String getPolicyName() {
            return policyName;
        }

        public void setPolicyName(String policyName) {
            this.policyName = policyName;
        }

        public String getPolicyNumber() {
            return policyNumber;
        }

        public void setPolicyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Object getStartDate() {
            return startDate;
        }

        public void setStartDate(Object startDate) {
            this.startDate = startDate;
        }

        public Object getEndDate() {
            return endDate;
        }

        public void setEndDate(Object endDate) {
            this.endDate = endDate;
        }
    }

    public class VehicleDetails {

        @SerializedName("vehicle_id")
        @Expose
        private Integer vehicleId;
        @SerializedName("vehicleName")
        @Expose
        private String vehicleName;
        @SerializedName("vehicleNumber")
        @Expose
        private String vehicleNumber;
        @SerializedName("frameNumber")
        @Expose
        private String frameNumber;
        @SerializedName("type")
        @Expose
        private Object type;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("insurancePolicy")
        @Expose
        private InsurancePolicy insurancePolicy;

        public Integer getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(Integer vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public String getFrameNumber() {
            return frameNumber;
        }

        public void setFrameNumber(String frameNumber) {
            this.frameNumber = frameNumber;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public InsurancePolicy getInsurancePolicy() {
            return insurancePolicy;
        }

        public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
            this.insurancePolicy = insurancePolicy;
        }
    }

    public class Datum {

        @SerializedName("vehicleDetails")
        @Expose
        private VehicleDetails vehicleDetails;

        public VehicleDetails getVehicleDetails() {
            return vehicleDetails;
        }

        public void setVehicleDetails(VehicleDetails vehicleDetails) {
            this.vehicleDetails = vehicleDetails;
        }
    }

}
