package com.example.biker112.ui.emergencyCont

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.apiManager.emergency.GetEmergencyContact
import com.example.biker112.ui.emergencyCont.adapter.EmergencyAdapter
import com.example.biker112.ui.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_emerg.*


class EmergencyContactFragment : BaseFragment(), View.OnClickListener, OnDeleteClickListener,
    OnPrefClickListener, OnDialogCloseListener {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var shareViewModel: EmergencyContactViewModel
    lateinit var adapter : EmergencyAdapter
    lateinit var recyclerViewVehicle : RecyclerView
    lateinit var textHome : TextView
    lateinit var ivPlus : ImageView
    private var contactList : ArrayList<GetEmergencyContact.Datum> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProviders.of(this).get(EmergencyContactViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_emerg, container, false)
        recyclerViewVehicle = root.findViewById(R.id.recyclerViewEmerg)
        ivPlus = root.findViewById(R.id.ivPlus)
        ivPlus.setOnClickListener(this)
        textHome = root.findViewById(R.id.text_home)
        val layoutManager = LinearLayoutManager(context)
        textHome.setText(resources.getText(R.string.emer_cont))
        recyclerViewVehicle?.layoutManager = layoutManager
        return root
    }

    override fun onResume() {
        super.onResume()
        getEmergency()
    }


    fun showDialog() {
        // Create the fragment and show it as a dialog.
        val newFragment = AddEmergencyDialogFragment.newInstance(this)
        newFragment.show(fragmentManager!!, "dialog")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivPlus ->{
                showDialog()
            }
        }
    }

    private fun getEmergency() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.getEmergencyContact(AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken,
                    SharedPreferencesManager.getInstance(context).userId).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                if(result.data.size != 0) {
                                    contactList = result.data
                                    adapter = EmergencyAdapter(context!!, contactList, this, this, this)
                                    recyclerViewVehicle?.adapter = adapter
                                    tvNOData.setVisibility(View.GONE)
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
        getEmergency()
       //adapter.notifyDataSetChanged()
    }

    override fun onPrefClick() {
       getEmergency()
    }

    override fun onDialogClose() {
       getEmergency()
    }


}