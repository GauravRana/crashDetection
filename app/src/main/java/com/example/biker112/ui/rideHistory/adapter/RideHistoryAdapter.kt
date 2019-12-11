package com.example.biker112.ui.rideHistory.adapter;

import android.view.LayoutInflater
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.biker112.R
import com.example.biker112.ui.apiManager.history.GetHistoryRes
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.emptyList
import kotlin.collections.ArrayList

public class RideHistoryAdapter(context: Context, historyList : ArrayList<GetHistoryRes.Datum>) : RecyclerView.Adapter<RideHistoryAdapter.ViewHolder>() {
        lateinit var mDataSet: ArrayList<GetHistoryRes.Datum>
        private val mInflater: LayoutInflater
        private val mContext: Context
        private val binderHelper = ViewBinderHelper()

        init {
                mContext = context
                mInflater = LayoutInflater.from(context)
                mDataSet = historyList
                // uncomment if you want to open only one row at a time
                // binderHelper.setOpenOnlyOne(true);
        }

        override fun getItemCount(): Int {
                if (mDataSet == null)
                        return 0
                return mDataSet.size
        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RideHistoryAdapter.ViewHolder {
                val view = mInflater.inflate(R.layout.adapter_layout_history, parent, false)
                return ViewHolder(view).listen { pos, type ->
                        //mContext.startActivity(Intent(mContext, NetworkActivity::class.java))
                }
        }

        override fun onBindViewHolder(h: ViewHolder, position: Int) {
                val holder = h as ViewHolder
                if (mDataSet != null && 0 <= position && position < mDataSet.size) {
                        val data = mDataSet.get(position)
                        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                        // put an unique string id as value, can be any string which uniquely define the data
                        // Bind your data here
                        holder.bind(mDataSet, position)
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

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private var tvStartDuration: TextView? = null
                private var tvEndDuration: TextView? = null
                private var tvStart: TextView? = null
                private var tvEnd: TextView? = null

                /**
                 * Find the Views in the layout<br></br>
                 * <br></br>
                 * Auto-created on 2019-09-09 12:25:58 by Android Layout Finder
                 * (http://www.buzzingandroid.com/tools/android-layout-finder)
                 */

                init {
                        tvStartDuration = itemView.findViewById(R.id.tvStartDuration)
                        tvEndDuration = itemView.findViewById(R.id.tvEndDuration)
                        tvStart = itemView.findViewById(R.id.tvStart)
                        tvEnd = itemView.findViewById(R.id.tvEnd)
                }

                fun bind(data: ArrayList<GetHistoryRes.Datum>, position: Int) {
                        try {
                                tvStart?.setText(getTime(data.get(position).tripDetails.startTime))
                                tvEnd?.setText(getTime(data.get(position).tripDetails.startTime))
                                tvStartDuration?.setText("Duration "+data.get(position).tripDetails.duration)
                                tvEndDuration?.setText("Distance "+data.get(position).tripDetails.distance)
                        } catch (e: Exception) {
                                e.printStackTrace()
                        }
                }

                private fun getTime(time: String): String{
                        val df = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")
                        val result: Date
                        var resString : String =""
                        try
                        {
                                result = df.parse(time)
                                println("date:" + result) //prints date in current locale
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
                                resString = sdf.format(result)
                                //System.out.println(sdf.format(result)) //prints date in the format sdf
                        }catch (e : Exception){
                                e.printStackTrace()
                        }
                        return resString
                }
        }

        fun <T : RideHistoryAdapter.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
                itemView.setOnClickListener {
                        event.invoke(getAdapterPosition(), getItemViewType())
                }
                return this
        }

        private fun startNetworkActivity(position: Int) {
                val bundle = Bundle()
                //mContext.startActivity(Intent(mContext, ServRequestDetailActivity::class.java).putExtras(bundle))

        }

        fun setData(newData: ArrayList<GetHistoryRes.Datum>) {
                this.mDataSet = newData
                notifyDataSetChanged()
        }



}