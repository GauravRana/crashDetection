package com.example.biker112.ui.utils;

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.biker112.R
import com.tapadoo.alerter.Alerter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


open class BaseActivity : AppCompatActivity() {
    private lateinit var processDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }


    fun showAlert(msg: String, type: String) {
        if (type.equals("error", ignoreCase = true)) {
            Alerter.create(this)
                .setText(msg)
                .setBackgroundColorRes(R.color.black)
                .setDuration(3000)
                .show()
        } else {
            Alerter.create(this)
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
                processDialog = ProgressDialog(this)
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

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

}
