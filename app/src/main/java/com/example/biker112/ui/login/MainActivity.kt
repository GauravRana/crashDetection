package com.example.biker112.ui.login

import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biker112.R
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.utils.AppConstants
import com.example.biker112.ui.utils.BaseActivity
import com.example.biker112.ui.utils.SharedPreferencesManager
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseActivity() , View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


    private lateinit var  drawerLayout : DrawerLayout
    ;
    private lateinit var  navView: NavigationView

    private lateinit var appBarConfiguration: AppBarConfiguration

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        ivMenu.setOnClickListener(this)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        setNavigationViewListener()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_start,
                R.id.nav_veh,
                R.id.nav_ride_h,
                R.id.nav_emer,
                R.id.nav_prof
            ), drawerLayout
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivMenu ->{
                getProfile()
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.getItemId()) {
            R.id.nav_sup -> {
                val callIntent = Intent(ACTION_CALL)
                callIntent.setData(Uri.parse("tel:" + "121"))
                startActivity(callIntent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setNavigationViewListener() {
        navView.setNavigationItemSelectedListener(this)
    }




    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }


    private fun getProfile() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.getProfile(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(this).authenticationToken,
                    SharedPreferencesManager.getInstance(this).userId).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                tvName.setText(result.data.displayName)
                                Glide.with(getApplicationContext())
                                    .load(result.data.photo)
                                    .apply(RequestOptions.placeholderOf(R.drawable.ic_place).error(R.drawable.ic_place))
                                    .into(profile_image)

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->

                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
