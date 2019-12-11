package com.example.biker112.ui.startRide

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crash_respond.*
import java.util.*


class CrashRespondActivity : BaseActivity(), View.OnClickListener {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private lateinit var countDownTimer: CountDownTimer
    private var barMax : Int  = 1
    var i = 0
    val t = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_respond)
        tvCancel.setOnClickListener(this)

        startTimer()
        ll_CS.setOnClickListener(View.OnClickListener {
            submitResponse("casual-stop")
        })

        ll_IN.setOnClickListener(View.OnClickListener {
            submitResponse("in-danger")
        })

        ll_MW.setOnClickListener(View.OnClickListener {
            submitResponse("accident")
        })

        ll_BI.setOnClickListener(View.OnClickListener {
            submitResponse("bike-issue")
        })
        ll_HP.setOnClickListener(View.OnClickListener {
            submitResponse(" health-problem")
        })



        t.scheduleAtFixedRate(object:TimerTask() {
            override fun run() {
                runOnUiThread(object:Runnable {
                    public override fun run() {
                        pgb_progress5.setAdProgress(i)
                        i++
                    }
                })
            }
        }, 0, 300)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvCancel ->{
               finish()
            }
        }
    }


    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textTimer.setText((millisUntilFinished / 1000).toString())
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                textTimer.setText("0")
                t.cancel()
                submitResponse("in-danger")
            }
        }.start()
    }


    private fun submitResponse(status : String) {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.submitCrashResponse(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(this).authenticationToken
                    ,sendResponseReq(status)).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                finish()
                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            //hideProgress()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->
                        showAlert(resources.getString(R.string.api_failure), "error")
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun sendResponseReq(status : String
    ): JsonObject {
        requestBody = JsonObject()
        requestBody.addProperty("id", SharedPreferencesManager.getInstance(this).tripId)
        requestBody.addProperty("end_time", SharedPreferencesManager.getInstance(this).currentLatitude)
        requestBody.addProperty("end_lat", SharedPreferencesManager.getInstance(this).currentLatitude)
        requestBody.addProperty("end_long", SharedPreferencesManager.getInstance(this).currentLongiitude)
        requestBody.addProperty("trip_status", status)
        requestBody.addProperty("distance",0)
        requestBody.addProperty("duration", 0)
        requestBody.addProperty("avg_speed", 0)
        return requestBody
    }




}
