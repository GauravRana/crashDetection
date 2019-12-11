package com.example.biker112.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import android.view.Gravity
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class LoginActivity : BaseActivity(), View.OnClickListener {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private var country_code = ""
    private var mobile_number = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ivNext.setOnClickListener(this)
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        ccp.setCountryForPhoneCode(91)
        KeyboardVisibilityEvent.setEventListener(this,
            object:KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen:Boolean) {
                    if(isOpen){
                        //llScreen.gravity = Gravity.CENTER
                    }else{
                        //llScreen.gravity = Gravity.BOTTOM
                    }
                }
            })
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivNext ->{
                mobile_number = etMobileNumber.text.toString()
                country_code = ccp.selectedCountryCode.toString()
                sendOTP(country_code, mobile_number)
            }
        }
    }


    private fun sendOTP(country_code: String, mobile_number: String) {
        try{
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.userOtp(sendOTPRequest("+"+country_code, mobile_number)).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                val intent = Intent(this@LoginActivity, OTPActivity::class.java)
                                    .putExtra("country_code",country_code)
                                    .putExtra("mobile_number", mobile_number)
                                startActivity(intent)
                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            hideProgress()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->
                        //showAlert("Error in sending", "error")
                    })
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun sendOTPRequest(country_code: String, mobile_number: String): JsonObject {
        requestBody = JsonObject()
        requestBody?.addProperty("country_code", country_code)
        requestBody?.addProperty("mobile_number", mobile_number)
        return requestBody
    }
}
