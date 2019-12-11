package com.example.biker112.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

    public class SharedPreferencesManager {
        private static final String PREF_NAME = "BIKER_PREFERENCE";
        private static final String AUTHENTICATION_PREF_NAME = "AUTHENTICATION_PREF_NAME";
        private static final String AUTHENTICATION_TOKEN = "authentication_token";


        private static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";
        private static final String CURRENT_LONGIITUDE = "CURRENT_LONGIITUDE";

        private static final String USER_ID = "USER_ID";
        private static final String TRIP_ID = "TRIP_ID";

        private static SharedPreferencesManager mInstance;
        private static Context mCtx;


        private SharedPreferencesManager(Context context) {
            mCtx = context;
        }

        public static synchronized SharedPreferencesManager getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new SharedPreferencesManager(context);
            }
            return mInstance;
        }


        public String getAuthenticationToken() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(AUTHENTICATION_TOKEN, "");
        }

        public boolean setAuthenticationToken(String newValue) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AUTHENTICATION_TOKEN, newValue);
            editor.apply();
            return true;
        }


        public String getUserId() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(USER_ID, "");
        }

        public boolean setUserId(String newValue) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_ID, newValue);
            editor.apply();
            return true;
        }


        public String getTripId() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(TRIP_ID, "");
        }

        public boolean setTripId(String newValue) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TRIP_ID, newValue);
            editor.apply();
            return true;
        }


        public String getCurrentLatitude() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(CURRENT_LATITUDE, "");
        }

        public boolean setCurrentLatitude(String newValue) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_LATITUDE, newValue);
            editor.apply();
            return true;
        }

        public String getCurrentLongiitude() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(CURRENT_LONGIITUDE, "");
        }

        public boolean setCurrentLongitude(String newValue) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_LONGIITUDE, newValue);
            editor.apply();
            return true;
        }

    }
