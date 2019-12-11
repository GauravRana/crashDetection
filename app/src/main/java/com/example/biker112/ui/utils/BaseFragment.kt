package com.example.biker112.ui.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.biker112.R
import com.tapadoo.alerter.Alerter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

open class BaseFragment : Fragment() {
    private lateinit var processDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun showAlert(msg: String, type: String) {
        if (type.equals("error", ignoreCase = true)) {
            Alerter.create(activity)
                .setText(msg)
                .setBackgroundColorRes(R.color.black)
                .setDuration(3000)
                .show()
        } else {
            Alerter.create(activity)
                .setText(msg)
                .setBackgroundColorRes(R.color.black)
                .show()
        }
    }


    fun showProgress() {
        try {
            if (processDialog != null) {
                //processDialog.hide();
                processDialog?.show()

            } else {
                processDialog = ProgressDialog(activity)
                processDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideProgress() {
        try {
            if (processDialog != null) {
                //processDialog.hide();
                processDialog?.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}