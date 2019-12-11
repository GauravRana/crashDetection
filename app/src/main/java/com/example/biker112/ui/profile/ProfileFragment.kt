package com.example.biker112.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseFragment
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.example.biker112.ui.utils.Utils
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlin.jvm.internal.Intrinsics

class ProfileFragment : BaseFragment(), View.OnClickListener {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private lateinit var viewModel: ProfileViewModel
    private lateinit var ivEdit : ImageView
    private lateinit var ivCamera : ImageView
    private lateinit var ivProfile : CircleImageView
    private lateinit var btn_verify : Button

    private lateinit var etName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etBlood : EditText
    private lateinit var etMobile : EditText
    private var imageURL = ""
    lateinit var ccp : com.hbb20.CountryCodePicker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        ivEdit = root.ivEdit.findViewById(R.id.ivEdit)
        ivCamera = root.ivCamera.findViewById(R.id.ivCamera)
        btn_verify = root.btn_verify.findViewById(R.id.btn_verify)
        ivProfile = root.ivProfile.findViewById(R.id.ivProfile)
        etName =  root.etName.findViewById(R.id.etName)
        etEmail =  root.etEmail.findViewById(R.id.etEmail)
        etBlood =  root.etBlood.findViewById(R.id.etBlood)
        etMobile =  root.etMobile.findViewById(R.id.etMobile)
        ccp = root.findViewById(R.id.ccp)
        ivEdit.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        btn_verify.setOnClickListener(this)
        ccp.isClickable = false
        etMobile.isEnabled = false
        iniEditFalse()
        getProfile()
        return root
    }


    override fun onResume() {
        super.onResume()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivEdit -> {
                iniEditTrue()
            }
            R.id.ivCamera -> {
                Utils().selectImagePickerF(this);
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

    private fun getProfile() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.getProfile(AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken,
                    SharedPreferencesManager.getInstance(context).userId).observeOn(
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
                                Glide.with(context!!)
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
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken
                    , sendProfileReq()
                    , SharedPreferencesManager.getInstance(context).userId
                ).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                getProfile()
                                iniEditFalse()
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

    private fun sendProfileReq(
    ): JsonObject {
        requestBody = JsonObject()
        requestBody?.addProperty("email", etEmail.text.toString())
        requestBody?.addProperty("blood_group",  etBlood.text.toString())
        requestBody?.addProperty("display_name", etName.text.toString())
        requestBody?.addProperty("display_name", etName.text.toString())
        requestBody?.addProperty("photo", imageURL)
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
                ivProfile.setImageBitmap(BitmapFactory.decodeFile(images[0].path))
                this.imageURL = Utils().encodeImage(imagePath)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


}