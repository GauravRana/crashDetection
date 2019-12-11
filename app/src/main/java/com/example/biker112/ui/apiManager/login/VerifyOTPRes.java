package com.example.biker112.ui.apiManager.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyOTPRes {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Data {

        @SerializedName("isNewUser")
        @Expose
        private Boolean isNewUser;
        @SerializedName("userRoleId")
        @Expose
        private Integer userRoleId;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("token")
        @Expose
        private String token;

        public Boolean getIsNewUser() {
            return isNewUser;
        }

        public void setIsNewUser(Boolean isNewUser) {
            this.isNewUser = isNewUser;
        }

        public Integer getUserRoleId() {
            return userRoleId;
        }

        public void setUserRoleId(Integer userRoleId) {
            this.userRoleId = userRoleId;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
