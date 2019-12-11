package com.example.biker112.ui.emergencyCont

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.OnDialogCloseListener
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.example.biker112.ui.utils.Utils
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_add_emerg.*
import kotlin.jvm.internal.Intrinsics

class AddEmergencyDialogFragment : AppCompatDialogFragment(), View.OnClickListener {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    lateinit var ivCross : ImageView
    lateinit var etName : EditText
    lateinit var btn_save : Button
    lateinit var etMobile : EditText
    lateinit var etEmail : EditText
    lateinit var etRel : EditText
    lateinit var ivCamera : ImageView
    lateinit var ccp : com.hbb20.CountryCodePicker
    lateinit var ivProfile : CircleImageView
    private lateinit var requestBody: JsonObject
    private var imageURL = ""

    companion object {
        private lateinit var listener: OnDialogCloseListener
        internal fun newInstance(listener: OnDialogCloseListener):AddEmergencyDialogFragment {
            this.listener = listener
            return AddEmergencyDialogFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.layout_add_emerg, container, false)
        ivCross  = v.findViewById(R.id.ivCross)
        btn_save = v.findViewById(R.id.btn_save)
        etName = v.findViewById(R.id.etName)
        etMobile = v.findViewById(R.id.etMobile)
        etEmail = v.findViewById(R.id.etEmail)
        etRel = v.findViewById(R.id.etRel)
        ccp = v.findViewById(R.id.ccp)
        ivCamera = v.findViewById(R.id.ivCamera)
        ivProfile = v.findViewById(R.id.ivProfile)
        ivCross.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        dialog?.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        iniIntent()

        etEmail.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s:CharSequence, start:Int,
                                  count:Int, after:Int) {}
            override fun onTextChanged(s:CharSequence, start:Int,
                              before:Int, count:Int) {
                tvError.setVisibility(View.GONE)
            }
        })
        return v
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivCross ->{
                dismiss()
            }
            R.id.btn_save ->{
                if(!isValidEmail(etEmail.text.toString())) {
                    tvError.setVisibility(View.VISIBLE)
                }else{
                    tvError.setVisibility(View.GONE)
                    addEmergency()
                }
            }
            R.id.ivCamera ->{
                Utils().selectImagePickerF(this);
            }
        }
    }


    //        override fun getTheme(): Int {
//            return R.style.FullScreenDialog
//        }

    private fun sendAddEmerReq(
    ): JsonObject {
        requestBody = JsonObject()
        requestBody?.addProperty("name", etName.text.toString())
        requestBody?.addProperty("email", etEmail.text.toString())
        requestBody?.addProperty("country_code",  ccp.selectedCountryCode)
        requestBody?.addProperty("mobile_number", etMobile.text.toString())
        requestBody?.addProperty("relation", etRel.text.toString())
        requestBody?.addProperty("photo", imageURL)
        return requestBody
    }



    private fun addEmergency() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.addEmergencyContact(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken
                    ,sendAddEmerReq()).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                AddEmergencyDialogFragment.listener.onDialogClose()
                                dismiss()
                            } else {
                                //showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            //hideProgress()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->
                        // showAlert(resources.getString(R.string.api_failure), "error")
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun iniIntent(){
        try{
            if(arguments!!.getString("name") != null || !arguments!!.getString("name").equals("")){
                etName.setText(arguments!!.getString("name"))
            }
            if(arguments!!.getString("email") != null || !arguments!!.getString("email").equals("")) {
                etEmail.setText(arguments!!.getString("email"))
            }
            if(arguments!!.getString("countryCode") != null || !arguments!!.getString("countryCode").equals("")) {
                ccp.setCountryForPhoneCode(arguments!!.getString("countryCode").toInt())
            }
            if(arguments!!.getString("mobile") != null || !arguments!!.getString("mobile").equals("")) {
                etMobile.setText(arguments!!.getString("mobile"))
            }
            if(arguments!!.getString("relation") != null || !arguments!!.getString("relation").equals("")) {
                etRel.setText(arguments!!.getString("relation"))
            }
            if(arguments!!.getString("photo") != null || !arguments!!.getString("photo").equals("")) {
                Glide.with(context!!)
                    .load(arguments!!.getString("photo"))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                    .into(ivProfile)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

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