package com.example.biker112.ui.rideHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.apiManager.history.GetHistoryRes
import com.example.biker112.ui.apiManager.vehicle.GetVehicleResponse
import com.example.biker112.ui.myVehicle.adapter.MyVehicleAdapter
import com.example.biker112.ui.rideHistory.adapter.RideHistoryAdapter
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseFragment
import com.example.biker112.ui.utils.SharedPreferencesManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_ride_history.*

class RideHistoryFragment : BaseFragment() {

    private lateinit var sendViewModel: RideHistoryViewModel
    lateinit var adapter : RideHistoryAdapter
    lateinit var recyclerViewrideHistory : RecyclerView
    lateinit var textHome : TextView
    lateinit var ivPlus : ImageView
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var historyList : ArrayList<GetHistoryRes.Datum> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
            ViewModelProviders.of(this).get(RideHistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_ride_history, container, false)
        recyclerViewrideHistory = root.findViewById(R.id.recyclerViewrideHistory)
        textHome = root.findViewById(R.id.text_home)
        ivPlus = root.findViewById(R.id.ivPlus)
        val layoutManager = LinearLayoutManager(context)
        ivPlus.visibility = View.INVISIBLE
        textHome.setText(resources.getText(R.string.ride_h))
        recyclerViewrideHistory?.layoutManager = layoutManager
        getRideHistory()
        return root
    }


    private fun getRideHistory() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.rideHistory(AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken, SharedPreferencesManager.getInstance(context).userId).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                if(result.data.size !=0) {
                                    historyList = result.data
                                    adapter = RideHistoryAdapter(context!!, historyList)
                                    recyclerViewrideHistory?.adapter = adapter
                                }else{
                                    tvNOData.setVisibility(View.VISIBLE)
                                }

                            } else {
                                //showAlert(resources.getString(R.string.api_failure), "error")
                                tvNOData.setVisibility(View.VISIBLE)
                                recyclerViewrideHistory?.adapter = null
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
}