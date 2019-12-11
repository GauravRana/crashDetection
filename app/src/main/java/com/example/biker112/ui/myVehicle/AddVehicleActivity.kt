package com.example.biker112.ui.myVehicle

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.example.biker112.ui.utils.Utils
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_vehicle.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlin.jvm.internal.Intrinsics

class AddVehicleActivity : BaseActivity(), View.OnClickListener,  AdapterView.OnItemSelectedListener {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var requestBody: JsonObject
    private var imageURL = ""
    var types = arrayOf<CharSequence>("Motorbikes", "Scooter/ moped", "E bikes/ Bicycle",
                                   "Motocross Bikes", "Racing Bikes", " Mountain Bikes", "Naked Bikes", "Enduro Bikes", "Cruisers" )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)
        btn_verify.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        findViews()
        iniIntent()
    }


    private fun addVehicle() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.addVehicle(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(this).authenticationToken
                    ,sendVehicleReq()).observeOn(
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



    private fun sendVehicleReq(
    ): JsonObject {
        requestBody = JsonObject()
        var jsonVehDet : JsonObject  = JsonObject()
        var jsonInsPol : JsonObject  = JsonObject()
        jsonVehDet.addProperty("vehicleName", etVehN.text.toString())
        jsonVehDet.addProperty("vehicleNumber", etFrame.text.toString())
        jsonVehDet.addProperty("frameNumber", etFrame.text.toString())
        jsonVehDet.addProperty("type", tvOrg.text.toString())
        jsonVehDet.addProperty("photo", imageURL)
        jsonVehDet.addProperty("vehicle_id",0)

        jsonInsPol.addProperty("policy_id", 0)
        jsonInsPol.addProperty("policy_name", etFrame.text.toString())
        jsonInsPol.addProperty("policy_number", etFrame.text.toString())
        jsonInsPol.addProperty("provider", etFrame.text.toString())
        jsonInsPol.addProperty("start_date", etFrame.text.toString())
        jsonInsPol.addProperty("end_date", etFrame.text.toString())
        requestBody.add("vehicleDetails", jsonVehDet)
        requestBody.add("insurancePolicy", jsonInsPol)
        return requestBody
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_verify ->{
                addVehicle()
            }
            R.id.ivCamera ->{
                Utils().selectImagePicker(this);
            }
        }
    }


    private fun iniIntent(){
        try{
            if (getIntent() != null) {
                if(getIntent().getStringExtra("vehicleName") != null || !getIntent().getStringExtra("vehicleName").equals("")){
                    etVehN.setText(getIntent().getStringExtra("vehicleName"))
                }
                if(getIntent().getStringExtra("vehicleNumber") != null || !getIntent().getStringExtra("vehicleNumber").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("vehicleNumber"))
                }
                if(getIntent().getStringExtra("frameNumber") != null || !getIntent().getStringExtra("frameNumber").equals("")){
                    etFrame.setText(getIntent().getStringExtra("frameNumber"))
                }
                if(getIntent().getStringExtra("type") != null || !getIntent().getStringExtra("type").equals("")){
                    tvOrg.setText(getIntent().getStringExtra("type"))
                }
                if(getIntent().getStringExtra("vehicle_id") != null || !getIntent().getStringExtra("vehicle_id").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("vehicle_id"))
                }
                if(getIntent().getStringExtra("policy_id") != null || !getIntent().getStringExtra("policy_id").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("policy_id"))
                }
                if(getIntent().getStringExtra("policy_name") != null || !getIntent().getStringExtra("policy_name").equals("")){
                    etInsN.setText(getIntent().getStringExtra("policy_name"))
                }
                if(getIntent().getStringExtra("policy_number") != null || !getIntent().getStringExtra("policy_number").equals("")){
                    etInsNo.setText(getIntent().getStringExtra("policy_number"))
                }
                if(getIntent().getStringExtra("provider") != null || !getIntent().getStringExtra("provider").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("provider"))
                }
                if(getIntent().getStringExtra("start_date") != null || !getIntent().getStringExtra("start_date").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("start_date"))
                }
                if(getIntent().getStringExtra("end_date") != null || !getIntent().getStringExtra("end_date").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("end_date"))
                }
                if(getIntent().getStringExtra("photo") != null || !getIntent().getStringExtra("photo").equals("")){
                    //etVehN.setText(getIntent().getStringExtra("photo"))
                    Glide.with(getApplicationContext())
                        .load(getIntent().getStringExtra("photo"))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                        .into(ivProfile)
                }
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

    private fun findViews() {
        spinner.setOnItemSelectedListener(this)
        val categories = ArrayList<String>()
        categories.add(types.get(0).toString())
        categories.add(types.get(1).toString())
        categories.add(types.get(2).toString())
        categories.add(types.get(3).toString())
        categories.add(types.get(4).toString())
        categories.add(types.get(5).toString())
        categories.add(types.get(6).toString())
        categories.add(types.get(7).toString())
        categories.add(types.get(8).toString())
        val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(dataAdapter)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()
        tvOrg.setText(item)
    }
}
