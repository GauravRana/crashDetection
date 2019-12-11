package com.example.biker112.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.login.MainActivity
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.example.biker112.ui.utils.Utils
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.jvm.internal.Intrinsics

class ProfileActivity : BaseActivity(), View.OnClickListener {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private var imageURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        ivEdit.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        btn_verify.setOnClickListener(this)
        ccp.isClickable = false
        etMobile.isEnabled = false
        iniEditFalse()
        getProfile()
    }


    override fun onResume() {
        super.onResume()

    }

    private fun getProfile() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.getProfile(AppConstants.BEARER + SharedPreferencesManager.getInstance(this).authenticationToken,
                    SharedPreferencesManager.getInstance(this).userId).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                etName.setText(result.data.displayName)
                                etEmail.setText(result.data.email)
                                etMobile.setText(result.data.mobileNumber)
                                ccp.setCountryForPhoneCode(result.data.countryCode.toInt())
                                etBlood.setText(result.data.bloodGroup)
                                Glide.with(getApplicationContext())
                                    .load(result.data.photo)
                                    .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                                    .into(ivProfile)

                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            hideProgress()
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


    private fun postProfile() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.postProfile(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(this).authenticationToken
                    , sendProfileReq()
                    ,  SharedPreferencesManager.getInstance(this).userId
                ).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                getProfile()
                                iniEditFalse()
                                val intent =
                                    Intent(this@ProfileActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            hideProgress()
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


    public fun iniEditFalse() {
        etName.isEnabled = false
        etMobile.isEnabled = false
        etEmail.isEnabled = false
        etBlood.isEnabled = false
        btn_verify.visibility = View.GONE
        ivCamera.visibility = View.GONE
    }

    public fun iniEditTrue() {
        etName.isEnabled = true
        etMobile.isEnabled = true
        etEmail.isEnabled = true
        etBlood.isEnabled = true
        btn_verify.visibility = View.VISIBLE
        ivCamera.visibility = View.VISIBLE
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivEdit -> {
                iniEditTrue()
            }
            R.id.ivCamera -> {
                Utils().selectImagePicker(this);
            }
            R.id.btn_verify -> {
                if(!isValidEmail(etEmail.text.toString())) {
                    showAlert("Email is Incorrect", "error");
                }else{
                    postProfile()
                }
            }
        }
    }


    private fun sendProfileReq(
    ): JsonObject {
        requestBody = JsonObject()
        requestBody?.addProperty("email", etEmail.text.toString())
        requestBody?.addProperty("blood_group",  etBlood.text.toString())
        requestBody?.addProperty("display_name", etName.text.toString())
        //requestBody?.addProperty("photo", otp)
        return requestBody
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(requestCode == 1) {
                val images = ImagePicker.getImages(data)
                val image = ImagePicker.getFirstImageOrNull(data)
                Intrinsics.checkExpressionValueIsNotNull(image, "image")
                val imagePath = image.path
                Intrinsics.checkExpressionValueIsNotNull(imagePath, "image.path")
                this.imageURL = Utils().encodeImage(imagePath)
                ivProfile.setImageBitmap(BitmapFactory.decodeFile(images[0].path))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

