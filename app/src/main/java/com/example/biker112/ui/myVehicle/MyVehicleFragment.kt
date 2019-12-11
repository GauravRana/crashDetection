package com.example.biker112.ui.myVehicle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.apiManager.emergency.GetEmergencyContact
import com.example.biker112.ui.apiManager.vehicle.GetVehicleResponse
import com.example.biker112.ui.emergencyCont.adapter.EmergencyAdapter
import com.example.biker112.ui.login.OTPActivity
import com.example.biker112.ui.myVehicle.adapter.MyVehicleAdapter
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseFragment
import com.example.biker112.ui.utils.OnDeleteClickListener
import com.example.biker112.ui.utils.SharedPreferencesManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_myvehicle.*
import kotlinx.android.synthetic.main.fragment_myvehicle.tvNOData
import kotlinx.android.synthetic.main.fragment_ride_history.*

class MyVehicleFragment : BaseFragment(), View.OnClickListener, OnDeleteClickListener {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var viewModel: MyVehicleViewModel
    lateinit var adapter : MyVehicleAdapter
    lateinit var recyclerViewVehicle : RecyclerView
    lateinit var textHome : TextView
    lateinit var ivPlus : ImageView
    private var vehicleList : ArrayList<GetVehicleResponse.Datum> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(MyVehicleViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_myvehicle, container, false)
        recyclerViewVehicle = root.findViewById(R.id.recyclerViewVehicle)
        textHome = root.findViewById(R.id.text_home)
        ivPlus = root.findViewById(R.id.ivPlus)
        ivPlus.setOnClickListener(this)
        textHome.setText(resources.getText(R.string.my_veh))
        val layoutManager = LinearLayoutManager(context)
        recyclerViewVehicle?.layoutManager = layoutManager
        return root
    }

    override fun onResume() {
        super.onResume()
        getVehicle()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivPlus ->{
                val intent = Intent(activity, AddVehicleActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getVehicle() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.getVehicle(AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken,
                    SharedPreferencesManager.getInstance(context).userId).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                if(result.data.size != 0) {
                                    vehicleList = result.data
                                    adapter = MyVehicleAdapter(context!!, vehicleList, this)
                                    recyclerViewVehicle?.adapter = adapter
                                }else{
                                    tvNOData.setVisibility(View.VISIBLE)
                                }

                            } else {
                                //showAlert(resources.getString(R.string.api_failure), "error")
                                tvNOData.setVisibility(View.VISIBLE)
                                recyclerViewVehicle?.adapter = null
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

    override fun onItemClick() {
        getVehicle()
        //adapter.notifyDataSetChanged()
    }

}