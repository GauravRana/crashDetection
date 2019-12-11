package com.example.biker112.ui.myVehicle.adapter;

import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.apiManager.vehicle.GetVehicleResponse
import com.example.biker112.ui.myVehicle.AddVehicleActivity
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.OnDeleteClickListener
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

public class MyVehicleAdapter(context: Context, vehicleList : ArrayList<GetVehicleResponse.Datum>, listener : OnDeleteClickListener) : RecyclerView.Adapter<MyVehicleAdapter.ViewHolder>() {
    var mDataSet : ArrayList<GetVehicleResponse.Datum> = ArrayList()
    private val mInflater: LayoutInflater
    private val mContext: Context
    private val binderHelper = ViewBinderHelper()
    private lateinit var  listener : OnDeleteClickListener

    init{
        mContext = context
        mDataSet = vehicleList
        this.listener = listener
        mInflater = LayoutInflater.from(context)
        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    override fun getItemCount(): Int {
        if (mDataSet == null)
            return 0
        return mDataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):MyVehicleAdapter.ViewHolder {
        val view = mInflater.inflate(R.layout.adapter_layout_vehicle, parent, false)
        return ViewHolder(view).listen { pos, type ->
                //mContext.startActivity(Intent(mContext, NetworkActivity::class.java))


        }
    }
    override fun onBindViewHolder(h: ViewHolder, position:Int) {
        val holder = h as ViewHolder
        if (mDataSet != null && 0 <= position && position < mDataSet.size)
        {
            val data = mDataSet.get(position)
            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            // Bind your data here
            holder.bind(mDataSet, position, listener)
        }


    }
    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    fun saveStates(outState: Bundle) {
        binderHelper.saveStates(outState)
    }
    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    fun restoreStates(inState: Bundle) {
        binderHelper.restoreStates(inState)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var swipeLayout1: SwipeRevealLayout ?= null
        private var tvEdit:TextView ?= null
        private var tvDelete:TextView ?= null
        private var ivVehImage:ImageView ?= null
        private var tvVehName:TextView ?= null
        private var tvVehDesc:TextView ?= null
        private var tvVehNo:TextView ?= null
        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        private lateinit var requestBodyDelete: JsonObject

        /**
         * Find the Views in the layout<br></br>
         * <br></br>
         * Auto-created on 2019-09-09 12:25:58 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */

        init{
            swipeLayout1 = itemView.findViewById(R.id.swipe_layout_1) as SwipeRevealLayout
            tvEdit = itemView.findViewById(R.id.tvEdit) as TextView
            tvDelete = itemView.findViewById(R.id.tvDelete) as TextView
            ivVehImage = itemView.findViewById(R.id.ivVehImage) as ImageView
            tvVehName = itemView.findViewById(R.id.tvVehName) as TextView
            tvVehDesc = itemView.findViewById(R.id.tvVehDesc) as TextView
            tvVehNo = itemView.findViewById(R.id.tvVehNo) as TextView

        }

        fun bind(data: ArrayList<GetVehicleResponse.Datum>, position: Int,listener : OnDeleteClickListener) {
            try{
                    tvVehName?.setText(data.get(position).vehicleDetails.vehicleName.toString())
                    tvVehNo?.setText(data.get(position).vehicleDetails.vehicleNumber.toString())
                    Glide.with(itemView.context)
                        .load(data.get(position).vehicleDetails.photo)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                        .into(ivVehImage!!)

                    itemView.setOnClickListener(View.OnClickListener {

                    })

                    tvEdit?.setOnClickListener(View.OnClickListener {
                        try{
                            val intent = Intent(itemView.context, AddVehicleActivity::class.java)
                                .putExtra("vehicleName", data.get(position).vehicleDetails.vehicleName.toString())
                                .putExtra("vehicleNumber", data.get(position).vehicleDetails.vehicleNumber.toString())
                                .putExtra("frameNumber",data.get(position).vehicleDetails.frameNumber.toString())
                                .putExtra("type", data.get(position).vehicleDetails.type.toString())
                                .putExtra("photo",data.get(position).vehicleDetails.photo.toString())
                                .putExtra("vehicle_id", data.get(position).vehicleDetails.vehicleId.toString())
                                .putExtra("policy_id", data.get(position).vehicleDetails.insurancePolicy.policyId.toString())
                                .putExtra("policy_name",data.get(position).vehicleDetails.insurancePolicy.policyName.toString())
                                .putExtra("policy_number", data.get(position).vehicleDetails.insurancePolicy.policyNumber.toString())
                                .putExtra("provider",data.get(position).vehicleDetails.insurancePolicy.provider.toString())
                                .putExtra("start_date", "")
                                .putExtra("end_date", "")
                            itemView.context.startActivity(intent)
                        }catch (e: Exception) {
                           e.printStackTrace()
                        }

                    })

                    tvDelete?.setOnClickListener(View.OnClickListener {
                        deleteEmergency(data.get(position).vehicleDetails.vehicleId.toString(), listener)
                    })




            }catch (e : Exception){
                e.printStackTrace()
            }
        }
        private fun deleteEmergency(id : String, listener : OnDeleteClickListener) {
            try {
                val repository = SearchRepositoryProvider.provideMainRepository(
                    AppConstants.API_URL)
                compositeDisposable.add(
                    repository.deleteVehicle(
                        AppConstants.BEARER + SharedPreferencesManager.getInstance(itemView.context).authenticationToken
                        ,sendDeleteReq(id)).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            try {
                                if (result.success) {
                                    listener.onItemClick()
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

        private fun sendDeleteReq(id : String
        ): JsonObject {
            requestBodyDelete = JsonObject()
            requestBodyDelete?.addProperty("id", id)
            return requestBodyDelete
        }
    }

    fun <T : MyVehicleAdapter.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

    private fun startNetworkActivity(position : Int){
        val bundle = Bundle()
        //mContext.startActivity(Intent(mContext, ServRequestDetailActivity::class.java).putExtras(bundle))

    }

    fun setData(newData: ArrayList<GetVehicleResponse.Datum>) {
        this.mDataSet = newData
        notifyDataSetChanged()
    }
}
