package com.example.biker112.ui.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.biker112.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class BaseApplication : Application(), Application.ActivityLifecycleCallbacks {
    override protected fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/montserrat.regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build())

    }

    override fun onActivityCreated(activity:Activity, savedInstanceState:Bundle) {
    }
    override fun onActivityStarted(activity:Activity) {
    }
    override fun onActivityResumed(activity:Activity) {
    }
    override fun onActivityPaused(activity:Activity) {
    }
    override fun onActivityStopped(activity:Activity) {
    }
    override fun onActivitySaveInstanceState(activity:Activity, outState:Bundle) {
    }
    override fun onActivityDestroyed(activity:Activity) {
    }
}