package com.example.biker112.ui.emergencyCont.adapter;

import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.biker112.R
import com.example.biker112.ui.apiManager.emergency.GetEmergencyContact
import de.hdodenhof.circleimageview.CircleImageView
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.emergencyCont.AddEmergencyDialogFragment
import com.example.biker112.ui.utils.*
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


public class EmergencyAdapter( context: Context, contactList :ArrayList<GetEmergencyContact.Datum>,listener : OnDeleteClickListener,clickListener : OnPrefClickListener,
                               dialogListener: OnDialogCloseListener) : RecyclerView.Adapter<EmergencyAdapter.ViewHolder>() {
        private val mInflater: LayoutInflater
        public val mContext: Context
        private var mDataSet : ArrayList<GetEmergencyContact.Datum> ?= null
        private val binderHelper = ViewBinderHelper()
        private lateinit var  listener : OnDeleteClickListener
        private lateinit var  clickListener : OnPrefClickListener
        public lateinit var dialogListener: OnDialogCloseListener


        init {
                mContext = context
                mDataSet = contactList
                this.listener = listener
                this.clickListener = clickListener
                this.dialogListener = dialogListener
                mInflater = LayoutInflater.from(context)
                // uncomment if you want to open only one row at a time
                // binderHelper.setOpenOnlyOne(true);
        }

        override fun getItemCount(): Int {
                if (mDataSet == null)
                        return 0
                return mDataSet!!.size
        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): EmergencyAdapter.ViewHolder {
                val view = mInflater.inflate(R.layout.adapter_layout_emerg, parent, false)
                return ViewHolder(mContext,view).listen { pos, type ->
                        //mContext.startActivity(Intent(mContext, NetworkActivity::class.java))
                }
        }

        override fun onBindViewHolder(h: ViewHolder, position: Int) {
                val holder = h as ViewHolder
                if (mDataSet != null && 0 <= position && position < mDataSet!!.size) {
                        val data = mDataSet!!.get(position)
                        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                        // put an unique string id as value, can be any string which uniquely define the data
                        // Bind your data here
                        holder.bind(mDataSet!!, position, listener, clickListener, dialogListener)
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

        class ViewHolder(context: Context,itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val swipeLayout1:SwipeRevealLayout
                private val ivImage:CircleImageView
                private val tvName:TextView
                private val tvRel:TextView
                private val llPhone:LinearLayout
                private val llMail:LinearLayout
                private val ivPref:ImageView
                private var phoneNumer : String =""
                private val tvEdit:TextView
                private val tvDelete : TextView
                val compositeDisposable: CompositeDisposable = CompositeDisposable()
                private lateinit var requestBodyPref: JsonObject
                private lateinit var requestBodyDelete: JsonObject
                private var iPref: Int = 0

                /**
                 * Find the Views in the layout<br></br>
                 * <br></br>
                 * Auto-created on 2019-09-09 12:25:58 by Android Layout Finder
                 * (http://www.buzzingandroid.com/tools/android-layout-finder)
                 */

                init {
                        swipeLayout1 = itemView.findViewById(R.id.swipe_layout_1) as SwipeRevealLayout
                        ivImage = itemView.findViewById(R.id.ivImage) as CircleImageView
                        tvName = itemView.findViewById(R.id.tvName) as TextView
                        tvRel = itemView.findViewById(R.id.tvRel) as TextView
                        llPhone = itemView.findViewById(R.id.llPhone) as LinearLayout
                        llMail = itemView.findViewById(R.id.llMail) as LinearLayout
                        ivPref = itemView.findViewById(R.id.ivPref) as ImageView
                        tvEdit = itemView.findViewById(R.id.tvEdit) as TextView
                        tvDelete = itemView.findViewById(R.id.tvDelete) as TextView

                }

                fun bind(data: ArrayList<GetEmergencyContact.Datum>, position: Int, listener : OnDeleteClickListener, clickListener : OnPrefClickListener, dialogListener: OnDialogCloseListener) {
                        try {
                                phoneNumer = data.get(position).contactDetails.countryCode+data.get(position).contactDetails.mobileNumber
                                tvName.setText(data.get(position).contactDetails.name)
                                tvRel.setText("Relation: "+data.get(position).contactDetails.relation)
                                if(data.get(position).contactDetails.preferredContact){
                                       ivPref.setImageResource(R.drawable.ic_pref_on)
                                        iPref = 1
                                }else{
                                       ivPref.setImageResource(R.drawable.ic_pref)
                                        iPref = 0
                                }

                                itemView.setOnClickListener(View.OnClickListener {

                                })

                                tvEdit.setOnClickListener(View.OnClickListener {
                                        showDialog(data.get(position).contactDetails.name,
                                                data.get(position).contactDetails.countryCode,
                                                data.get(position).contactDetails.mobileNumber,
                                                data.get(position).contactDetails.email,
                                                data.get(position).contactDetails.relation,
                                                data.get(position).contactDetails.photo.toString(),
                                                dialogListener)
                                })

                                tvDelete.setOnClickListener(View.OnClickListener {
                                        deleteEmergency(data.get(position).contactDetails.id.toString(), listener)
                                })

                                ivPref.setOnClickListener(View.OnClickListener {
                                        if(iPref == 0){
                                            iPref = 1
                                        }
                                        else if(iPref == 1){
                                            iPref = 0
                                        }
                                        updatePeference(data.get(position).contactDetails.id.toString(), iPref, clickListener)
                                })

                                llPhone.setOnClickListener(View.OnClickListener {
                                        val intent = Intent(ACTION_DIAL)
                                        intent.setData(Uri.parse("tel:"+phoneNumer))
                                        itemView.context.startActivity(intent)
                                })

                                llMail.setOnClickListener(View.OnClickListener {
                                        val i = Intent(Intent.ACTION_SEND)
                                        i.setType("message/rfc822")
                                        i.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("recipient@example.com"))
                                        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
                                        i.putExtra(Intent.EXTRA_TEXT, "body of email")
                                        try
                                        {
                                                itemView.context.startActivity(Intent.createChooser(i, "Send mail..."))
                                        }
                                        catch (ex:android.content.ActivityNotFoundException) {
                                               // Toast.makeText(this@MyActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
                                        }
                                })
                                Glide.with(itemView.context)
                                        .load(data.get(position).contactDetails.photo)
                                        .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                                        .into(ivImage)
                        } catch (e: Exception) {
                                e.printStackTrace()
                        }
                }

                fun showDialog(name: String,
                               countryCode: String,
                               mobile: String,
                               email: String,
                               relation: String,
                               photo: String,
                               dialogListener: OnDialogCloseListener) {
                        // Create the fragment and show it as a dialog.
                        val newFragment = AddEmergencyDialogFragment.newInstance(dialogListener)
                        val bundle = Bundle()
                        bundle.putString("name", name)
                        bundle.putString("countryCode", countryCode)
                        bundle.putString("mobile", mobile)
                        bundle.putString("email", email)
                        bundle.putString("relation", relation)
                        bundle.putString("photo", photo)
                        newFragment.setArguments(bundle)
                        newFragment.show((itemView.context as AppCompatActivity).getSupportFragmentManager(), "Title")

                }

                private fun updatePeference(id : String,
                                            isPref : Int,
                                            clickListener : OnPrefClickListener
                ) {
                        try {
                                val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
                                compositeDisposable.add(
                                        repository.updatePreference(
                                                AppConstants.BEARER + SharedPreferencesManager.getInstance(itemView.context).authenticationToken
                                                ,sendPrefReq(id, isPref)).observeOn(
                                                AndroidSchedulers.mainThread()
                                        )
                                                .subscribeOn(Schedulers.io())
                                                .subscribe({ result ->
                                                        try {
                                                                if (result.success) {
                                                                        clickListener.onPrefClick()
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


                private fun deleteEmergency(id : String, listener : OnDeleteClickListener) {
                        try {
                                val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
                                compositeDisposable.add(
                                        repository.deleteEmergencyContact(
                                                AppConstants.BEARER + SharedPreferencesManager.getInstance(itemView.context).authenticationToken
                                                ,id).observeOn(
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

                private fun sendPrefReq(id : String,
                                        isPref : Int
                ): JsonObject {
                        requestBodyPref = JsonObject()
                        requestBodyPref?.addProperty("id", id)
                        requestBodyPref?.addProperty("isPrefer",  isPref)
                        return requestBodyPref
                }



                private fun sendDeleteReq(id : String
                ): JsonObject {
                        requestBodyDelete = JsonObject()
                        requestBodyDelete?.addProperty("id", id)
                        return requestBodyDelete
                }

        }

        fun <T : EmergencyAdapter.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
                itemView.setOnClickListener {
                        event.invoke(getAdapterPosition(), getItemViewType())
                }

                return this
        }

        private fun startNetworkActivity(position: Int) {
                val bundle = Bundle()
                //mContext.startActivity(Intent(mContext, ServRequestDetailActivity::class.java).putExtras(bundle))

        }

        fun setData(newData: ArrayList<GetEmergencyContact.Datum>) {
                this.mDataSet = newData
                notifyDataSetChanged()
        }






}