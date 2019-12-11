package com.example.biker112.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.profile.ProfileActivity
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_otp.*


class OTPActivity : BaseActivity(), View.OnClickListener {

    private var hidePhoneNumber : String = ""
    private var showPhoneNumber : String = ""
    private var verifyText : String = ""
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private var country_code = ""
    private var mobile_number = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        btn_verify.setOnClickListener(this)
        iniIntent()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_verify ->{
               if(otp_view?.text.toString().contentEquals("")){
                   showAlert(resources.getString(R.string.error_otp), "error")
               }else{
                   verifyOTP(country_code, mobile_number, otp_view.text.toString())
               }
            }

        }
    }

    public fun iniIntent(){
        try{
            if (getIntent() != null) {
                mobile_number = getIntent().getStringExtra("mobile_number")
                country_code = getIntent().getStringExtra("country_code")
                val myNumber = StringBuilder(mobile_number)
                if(myNumber.length > 2) {
                    for (i in 0..myNumber.length) {
                        if (i > 2 && i < myNumber.length - 1) {
                            myNumber.setCharAt(i, '*')
                        }
                    }
                    tvVerify.setText("Verification code sent to your number " + "+" +country_code +" "+myNumber)
                }else{
                    tvVerify.setText("Verification code sent to your number " + "+" +country_code +" "+"***")
                }

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun verifyOTP(country_code: String, mobile_number: String, otp :String) {
        try{
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.verifyOTP(sendVerifyRequest("+"+country_code, mobile_number, otp)).observeOn(
                    AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                SharedPreferencesManager.getInstance(this).setAuthenticationToken(result.data.token)
                                SharedPreferencesManager.getInstance(this).setUserId(result.data.id.toString())
                                if(result.data.isNewUser) {
                                    val intent =
                                        Intent(this@OTPActivity, ProfileActivity::class.java)
                                            .putExtra("isNewUser", result.data.isNewUser)
                                    startActivity(intent)
                                }else{
                                    val intent =
                                        Intent(this@OTPActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            } else {
                                showAlert(result.message.toString(), "error")
                            }
                            hideProgress()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->
                        showAlert(resources.getString(R.string.api_failure), "error")
                    })
            )
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun sendVerifyRequest(country_code: String, mobile_number: String, otp : String): JsonObject {
        requestBody = JsonObject()
        requestBody?.addProperty("country_code", country_code)
        requestBody?.addProperty("mobile_number", mobile_number)
        requestBody?.addProperty("otp", otp)
        return requestBody
    }
}
