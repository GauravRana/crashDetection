package com.example.biker112.ui.startRide

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.biker112.R
import com.example.biker112.ui.Data
import com.example.biker112.ui.apiManager.SearchRepositoryProvider
import com.example.biker112.ui.apiManager.vehicle.GetVehicleResponse
import com.example.biker112.ui.gps.GpsServices
import com.example.biker112.ui.utils.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_start_ride.*
import java.util.*

class StartRideFragment : BaseFragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener,
    OnItemClickListener, IBaseGpsListener, GpsStatus.Listener {
    private lateinit var viewModel: StartRideViewModel
    private var mGoogleMap: GoogleMap? = null
    private var mapView: MapView? = null
    private val MY_PERMISSIONS_REQUEST_LOCATION: Int = 99
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<CardView>;
    private lateinit var bottomSheetLayout : CardView
    private lateinit var btn_start : CircleImageView
    private lateinit var tvCancel : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var llTimer : LinearLayout
    private lateinit var llVehList : LinearLayout
    private lateinit var ChTimer : Chronometer
    private lateinit var ivPlay : ImageView
    private lateinit var ivStop : ImageView
    private lateinit var ivSOS : ImageView
    private  var isplaying : Boolean = false
    private  var speed : Double = 0.0
    private var distance : Double  = 0.0;
    private  var lStartLoc : Location ?= null
    private  var lEndLoc : Location ?= null
    private val MY_PERMISSION_ACCESS_COARSE_LOCATION = 11
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    lateinit var adapter : MyAdapter
    private var vehicleList : ArrayList<GetVehicleResponse.Datum> = ArrayList()
    private lateinit var requestRoutesBody: JsonObject
    private lateinit var requestBody: JsonObject
    private lateinit var sharedPreferences:SharedPreferences
    private var onGpsServiceUpdate : Data.OnGpsServiceUpdate ?= null

    var timeWhenStopped :Long = 0L ;
    private var firstfix = false

    companion object Foo{
        private var data: Data = Data()
        var startTime : Long = 0L
        var tvSpeed : TextView ?= null
        var tvDistance : TextView ?= null

        fun getData():Data {
            return data
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstancetvVehState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(StartRideViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_start_ride, container, false)
        bottomSheetLayout  = root.findViewById(R.id.bottom_sheet_layout)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        data = Data(onGpsServiceUpdate)
        btn_start =  root.findViewById(R.id.btn_start)
        llTimer =  root.findViewById(R.id.llTimer)
        llVehList =  root.findViewById(R.id.llVehList)
        recyclerView = root.findViewById(R.id.recyclerView)
        tvCancel = root.findViewById(R.id.tvCancel)
        ChTimer = root.findViewById(R.id.ChTimer)
        ivPlay = root.findViewById(R.id.ivPlay)
        ivStop = root.findViewById(R.id.ivStop)
        tvSpeed = root.findViewById(R.id.tvSpeed)
        tvDistance  = root.findViewById(R.id.tvDistance)
        ivSOS = root.findViewById(R.id.ivSOS)
        ivSOS.setOnClickListener(this)
        ivPlay.setOnClickListener(this)
        ivStop.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        btn_start.setOnClickListener(this)
        setTimer()
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        bottomSheetBehavior = BottomSheetBehavior.from<CardView>(bottomSheetLayout)
        bottomSheetBehavior.setPeekHeight(0);
        viewModel.text.observe(this, Observer {

        })
        //ivMenu.setOnClickListener(this)
        //navView = root.findViewById(R.id.nav_view)
        mapView = root.findViewById(R.id.map)
        mapView?.onCreate(savedInstancetvVehState)
        mapView?.onResume()
        mapView?.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!);
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })



        onGpsServiceUpdate = object:Data.OnGpsServiceUpdate {
            override fun update() {
                var maxSpeedTemp = data.getMaxSpeed()
                var distanceTemp = data.getDistance()
                var averageTemp:Double
                if (sharedPreferences.getBoolean("auto_average", false))
                {
                    averageTemp = data.getAverageSpeedMotion()
                }
                else
                {
                    averageTemp = data.getAverageSpeed()
                }
                val speedUnits:String
                val distanceUnits:String
                if (sharedPreferences.getBoolean("miles_per_hour", false))
                {
                    maxSpeedTemp *= 0.62137119
                    distanceTemp = distanceTemp / 1000.0 * 0.62137119
                    averageTemp *= 0.62137119
                    speedUnits = "mi/h"
                    distanceUnits = "mi"
                }
                else
                {
                    speedUnits = "km/h"
                    if (distanceTemp <= 1000.0)
                    {
                        distanceUnits = "m"
                    }
                    else
                    {
                        distanceTemp /= 1000.0
                        distanceUnits = "km"
                    }
                }
                var s = SpannableString(
                    String.format(
                        "%.0f %s",
                        maxSpeedTemp,
                        speedUnits
                    )
                )
                s.setSpan(
                    RelativeSizeSpan(0.5f),
                    s.length - speedUnits.length - 1,
                    s.length,
                    0
                )
                //maxSpeed.setText(s)
                s = SpannableString(String.format("%.0f %s", averageTemp, speedUnits))
                s.setSpan(
                    RelativeSizeSpan(0.5f),
                    s.length - speedUnits.length - 1,
                    s.length,
                    0
                )
                //averageSpeed.setText(s)
                s = SpannableString(String.format("%.3f %s", distanceTemp, distanceUnits))
                s.setSpan(
                    RelativeSizeSpan(0.5f),
                    s.length - distanceUnits.length - 1,
                    s.length,
                    0
                )
                tvDistance!!.setText(s)
            }
        }

        return root
    }


    private fun setTimer(){
        ChTimer.setOnChronometerTickListener(object: Chronometer.OnChronometerTickListener {
            override fun onChronometerTick(chronometer:Chronometer) {
                val time = SystemClock.elapsedRealtime() - chronometer.getBase()
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - (h * 3600000).toLong() - (m * 60000).toLong()).toInt() / 1000
                val t = (if (h < 10) "0" + h else h).toString() + ":" + (if (m < 10) "0" + m else m) + ":" + (if (s < 10) "0" + s else s)
                chronometer.setText(t)
            }
        })
        ChTimer.setBase(SystemClock.elapsedRealtime())
        ChTimer.setText("00:00:00")
    }


    override fun onResume() {
        super.onResume()
        try{
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            LocalBroadcastManager.getInstance(context!!).registerReceiver(bReceiver,  IntentFilter("message"));
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGpsDisabledDialog()
            }
            firstfix = true
            if (!data.isRunning())
            {
                val gson = Gson()
                val json = sharedPreferences.getString("data", "")
                data = gson.fromJson(json, Data::class.java)
            }
            if (data == null)
            {
                data = Data(onGpsServiceUpdate)
            }
            else
            {
                data.setOnGpsServiceUpdate(onGpsServiceUpdate)
            }
            if (locationManager!!.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0)
            {
                if (checkSelfPermission(context!! ,Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    // Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    // int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return
                }
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0f, this)
            }
            else
            {

            }
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                //showGpsDisabledDialog();
            }
            locationManager!!.addGpsStatusListener(this)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }




    override fun onMapReady(googleMap: GoogleMap) {
        try{
            mGoogleMap = googleMap;
            mGoogleMap!!.getUiSettings().setCompassEnabled(false);
            mGoogleMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    //Location Permission already granted
                    buildGoogleApiClient();
                    mGoogleMap?.setMyLocationEnabled(true);
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                }
            } else {
                buildGoogleApiClient();
                mGoogleMap?.setMyLocationEnabled(true);


            }
        }catch (e: Exception){

        }

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient?.connect()
    }


    override fun onPause() {
        super.onPause()
        try{
            if (mGoogleApiClient != null) {
                mFusedLocationClient?.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        getNowPosition(location)
                    }
                //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(bReceiver);

            locationManager!!.removeUpdates(this)
            locationManager!!   .removeGpsStatusListener(this)
            val prefsEditor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(data)
            prefsEditor.putString("data", json)
            prefsEditor.commit()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        context?.stopService(Intent(context, GpsServices::class.java))
    }

    override fun onConnected(p0: Bundle?) {
        try{
            mLocationRequest = LocationRequest();
            mLocationRequest?.setInterval(1000);
            mLocationRequest?.setFastestInterval(1000);
            mLocationRequest?.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocationClient?.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        getNowPosition(location)
                    }
                //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {
        //getNowPosition(location)
        try{
            if (location!!.hasAccuracy())
            {
                var acc = location.getAccuracy()
                val units:String
                if (sharedPreferences.getBoolean("miles_per_hour", false))
                {
                    units = "ft"
                    acc *= 3.28084f
                }
                else
                {
                    units = "m"
                }
                val s = SpannableString(String.format("%.0f %s", acc, units))
                s.setSpan(RelativeSizeSpan(0.75f), s.length - units.length - 1, s.length, 0)
                //accuracy.setText(s)
                if (firstfix)
                {

                    firstfix = false
                }
            }
            else
            {
                firstfix = true
            }
            if (location.hasSpeed())
            {
                //progressBarCircularIndeterminate.setVisibility(View.GONE);
                var speed = location.getSpeed() * 3.6
                val units:String
                if (sharedPreferences.getBoolean("miles_per_hour", false))
                { // Convert to MPH
                    speed *= 0.62137119
                    units = "mi/h"
                }
                else
                {
                    units = "km/h"
                }
                val s = SpannableString(
                    String.format(
                        Locale.ENGLISH,
                        "%.0f %s",
                        speed,
                        units
                    )
                )
                s.setSpan(RelativeSizeSpan(0.25f), s.length - units.length - 1, s.length, 0)
                tvSpeed?.setText(s)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }


    }


    private fun updateSpeed(location:CLocation) {
        // TODO Auto-generated method stub
        var nCurrentSpeed = 0f
        if (location != null)
        {
            nCurrentSpeed = location.getSpeed()
        }
        val fmt = Formatter(StringBuilder())
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed)
        var strCurrentSpeed = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0')
        val strUnits = "miles/hour"
        tvSpeed?.setText(strCurrentSpeed + " " + strUnits)
    }

    public fun getNowPosition(location: Location?) {
        try{
            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker?.remove();
            }

            if (lStartLoc == null) {
                lStartLoc = mLastLocation;
                lEndLoc = mLastLocation;
            } else
                lEndLoc = mLastLocation;

            //Place current location marker
            lateinit var latLng: LatLng
            latLng = LatLng(location!!.getLatitude(), location!!.getLongitude());
            SharedPreferencesManager.getInstance(context).setCurrentLatitude(location!!.getLatitude().toString())
            SharedPreferencesManager.getInstance(context).setCurrentLongitude(location!!.getLongitude().toString())
            lateinit var markerOptions: MarkerOptions
            markerOptions = MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            mCurrLocationMarker = mGoogleMap?.addMarker(markerOptions);
            val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.flag)
            mCurrLocationMarker?.setAnchor(0.5f, 0.5f)
            mCurrLocationMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flag", 150, 150)))


            //move map camera
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 50f));
            val cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                    )
                ) // Sets the center of the map to location user
                .zoom(17f) // Sets the zoom
                .bearing(90f) // Sets the orientation of the camera to east
                .tilt(40f) // Sets the tilt of the camera to 30 degrees
                .build() // Creates a CameraPosition from the builder
            mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            setCurrentLocation()
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                if (location.hasSpeed()) {
                    speed = (location.speed) * 3.6
                    val s = SpannableString(String.format(Locale.ENGLISH, "%.0f %s", speed, "km/h"))
                    s.setSpan(RelativeSizeSpan(0.25f), s.length - "km/h".length - 1, s.length, 0)
                    updateSpeedDistance(s);
                }

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun resizeMapIcons(iconName:String, width:Int, height:Int):Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", context?.packageName))
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return resizedBitmap
    }



    private fun updateSpeedDistance(s: SpannableString) {
       //tvSpeed.setText(s)
    }


    fun setCurrentLocation() {
        try {
            if ((Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED)
            ) {//requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
                // return;
            } else {
                getCurrentAddress()
            }
        } catch (e: Exception) {

        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private fun checkLocationPermission() {
        if ((ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED)
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(context!!)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        }
                    })
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if ((ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED)
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mGoogleMap?.setMyLocationEnabled(true)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
// permissions this app might request
    }

    fun getCurrentAddress() {
        // Get the location manager
        if (locationManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) !== android.content.pm.PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        context!!, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) !== android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    1f, this
                )
            } catch (ex: Exception) {
                Log.i("msg", "fail to request location update, ignore", ex)
            }
            if (locationManager != null) {
                mLastLocation =
                    locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            val gcd = Geocoder(
                context,
                Locale.getDefault()
            )
            val addresses: List<Address>
            try {
                addresses = gcd.getFromLocation(
                    mLastLocation!!.getLatitude(),
                    mLastLocation!!.getLongitude(), 1
                )
                if (addresses.size > 0) {
                    lateinit var currentLocation: String
                    lateinit var current_locality: String
                    val address = addresses.get(0)
                        .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val locality = addresses.get(0).getLocality()
                    val subLocality = addresses.get(0).getSubLocality()
                    val state = addresses.get(0).getAdminArea()
                    val country = addresses.get(0).getCountryName()
                    val postalCode = addresses.get(0).getPostalCode()
                    val knownName = addresses.get(0).getFeatureName()
                    if (subLocality != null) {
                        currentLocation = locality + "," + subLocality
                    } else {
                        currentLocation = locality
                    }
                    current_locality = locality
                    tvAddress.setText(currentLocation)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }






    fun resetData() {
        //fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        //refresh.setVisibility(View.INVISIBLE);
        tvSpeed?.setText("")
        tvDistance?.setText("")
        ChTimer.setText("00:00:00")
        data = Data(onGpsServiceUpdate)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_start ->{
               //vehListON()
               timerON()
               getVehicle()
               bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            R.id.ivSOS ->{
                val intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:"+"121"))
                context?.startActivity(intent)
            }
            R.id.tvCancel ->{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            R.id.ivPlay ->{
                if(isplaying == false){
                    isplaying = true
                    ivPlay.setImageResource(R.drawable.pause_button)
                    ChTimer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    ChTimer.start()
                    //FALL DETECTION BACKGROUND AND FOREGROUND SERVICE
                    startCrashDetectionService()
                    //callRoutesEverySecond("START")

                    if (!StartRideFragment.data.isRunning) {
                        StartRideFragment.data.isRunning = true
                        StartRideFragment.data.isFirstTime = true
                        startGPSService()
                    } else { //fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                        StartRideFragment.data.isRunning = false
                        context!!.stopService(Intent(context, GpsServices::class.java))
                    }


                }else{
                    isplaying = false
                    ivPlay.setImageResource(R.drawable.play_button)
                    timeWhenStopped = ChTimer.getBase() - SystemClock.elapsedRealtime();
                    ChTimer.stop()
                    submitResponse("pause")
                    //callRoutesEverySecond("STOP")
                }
            }
            R.id.ivStop ->{
                isplaying = false
                ChTimer.stop()
                timeWhenStopped = 0L
                ivPlay.setImageResource(R.drawable.play_button)
                stopCrashDetectionService()
                submitResponse("end")
                resetData()
                context!!.stopService(Intent(context, GpsServices::class.java))

                //callRoutesEverySecond("STOP")
            }
        }
    }


    public fun timerON() {
        llVehList.visibility = View.GONE
        llTimer.visibility = View.VISIBLE
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
    }


    private fun vehListON() {
        llVehList.visibility = View.VISIBLE
        llTimer.visibility = View.GONE
    }


    private fun startCrashDetectionService() {
        val intent = Intent(context, CrashDetectionService::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(context!!, intent)
        } else {
            context!!.startService(intent)
        }
    }


    private fun stopCrashDetectionService() {
        val intent = Intent(context, CrashDetectionService::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            context!!.stopService(intent)
        } else {
            context!!.stopService(intent)
        }
    }


    private fun startGPSService() {
        val intent = Intent(context, GpsServices::class.java)
        StartRideFragment.startTime = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(context!!, intent)
        } else {    
            context!!.startService(intent)
        }
    }

    private val bReceiver = object: BroadcastReceiver() {
        override fun onReceive(context:Context, intent:Intent) {
            context!!.startActivity(Intent(context!!, CrashRespondActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun getData():Data {
        return data
    }



    fun showGpsDisabledDialog() {
        if (ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSION_ACCESS_COARSE_LOCATION
            )
        }
        //locationManager?.addGpsStatusListener(this)
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0f, this)
        startActivity(Intent("android.settings.LOCATION_SOURCE_SETTINGS"))
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
                                vehicleList = result.data
                                adapter = MyAdapter(context!!, vehicleList, this)
                                recyclerView?.adapter = adapter

                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
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
        timerON()
    }



    class MyAdapter(context: Context, vehicleList : ArrayList<GetVehicleResponse.Datum>,listener : OnItemClickListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        var mDataSet : ArrayList<GetVehicleResponse.Datum> = ArrayList()
        private val mInflater: LayoutInflater
        private val mContext: Context
        private val binderHelper = ViewBinderHelper()
        private lateinit var  listener : OnItemClickListener


        init{
            mContext = context
            mDataSet = vehicleList
            mInflater = LayoutInflater.from(context)
            this.listener = listener
            // uncomment if you want to open only one row at a time
            // binderHelper.setOpenOnlyOne(true);
        }

        override fun getItemCount(): Int {
            if (mDataSet == null)
                return 0
            return mDataSet.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):MyAdapter.ViewHolder {
            val view = mInflater.inflate(R.layout.adapter_bottom_sheet, parent, false)
            return ViewHolder(view).listen { pos, type ->
                //mContext.startActivity(Intent(mContext, NetworkActivity::class.java))
                //timerOn()

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
            private var tvVeh: TextView? = null
            private lateinit var requestBodyStartTrip: JsonObject

            /**
             * Find the Views in the layout<br></br>
             * <br></br>
             * Auto-created on 2019-09-09 12:25:58 by Android Layout Finder
             * (http://www.buzzingandroid.com/tools/android-layout-finder)
             */

            init {
                tvVeh = itemView.findViewById(R.id.tvVeh) as TextView
            }

            fun bind(data: ArrayList<GetVehicleResponse.Datum>, position: Int, listener : OnItemClickListener) {
                try {
                    tvVeh?.setText(data.get(position).vehicleDetails.vehicleNumber.toString())
                    val start_time = getDateTime()
                    val user_vehicle_id = data.get(position).vehicleDetails.vehicleId.toString()
                    val start_lat = SharedPreferencesManager.getInstance(itemView.context).currentLatitude
                    val start_long = SharedPreferencesManager.getInstance(itemView.context).currentLongiitude
                    val trip_status = "start"
                    val locale: String = "nl"

                    tvVeh?.setOnClickListener(View.OnClickListener {
                        startTrip(listener,
                            start_time,
                            user_vehicle_id,
                            start_lat,
                            start_long,
                            trip_status,
                            locale)
                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fun localToGMT():Date {
                val date = Date()
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
                val gmt = Date(sdf.format(date))
                return gmt
            }


            fun getDateTime():String{
                var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                var currentDateandTime = sdf.format(Date())
                return currentDateandTime
            }



            private fun startTrip(listener : OnItemClickListener,
                                  start_time: String,
                                  user_vehicle_id: String,
                                  start_lat: String,
                                  start_long: String,
                                  trip_status: String,
                                  locale: String
                                ) {
                try {
                    val compositeDisposable: CompositeDisposable = CompositeDisposable()
                    val repository = SearchRepositoryProvider.provideMainRepository(
                        AppConstants.API_URL
                    )
                    compositeDisposable.add(
                        repository.startTrip(
                            AppConstants.BEARER + SharedPreferencesManager.getInstance(itemView.context).authenticationToken
                            , startTripReq(start_time,
                                user_vehicle_id,
                                start_lat,
                                start_long,
                                trip_status,
                                locale)
                        ).observeOn(
                            AndroidSchedulers.mainThread()
                        )
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                try {
                                    if (result.success) {
                                        SharedPreferencesManager.getInstance(itemView.context).setTripId(result.data.id.toString())
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


            private fun startTripReq(
                start_time: String,
                user_vehicle_id: String,
                start_lat: String,
                start_long: String,
                trip_status: String,
                locale: String
            ): JsonObject {
                requestBodyStartTrip = JsonObject()
                requestBodyStartTrip?.addProperty("start_time", start_time)
                requestBodyStartTrip?.addProperty("user_vehicle_id", user_vehicle_id)
                requestBodyStartTrip?.addProperty("start_lat", start_lat)
                requestBodyStartTrip?.addProperty("start_long", start_long)
                requestBodyStartTrip?.addProperty("trip_status", trip_status)
                requestBodyStartTrip?.addProperty("locale", locale)
                return requestBodyStartTrip
            }
        }

        fun <T : MyAdapter.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
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



    private fun submitRoutes() {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.addRoutes(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(activity).authenticationToken
                    ,sendRoutesReq()).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                //finish()
                            } else {
                                showAlert(resources.getString(R.string.api_failure), "error")
                            }
                            //hideProgress()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, { error ->
                        //showAlert(resources.getString(R.string.api_failure), "error")
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun sendRoutesReq(
    ): JsonObject {
        var i: Long
        requestRoutesBody = JsonObject()
        var jsonRoutePoints : JsonArray  = JsonArray()
        for (i in 0..2) {
            var jsonObj : JsonObject =  JsonObject();
            jsonObj.addProperty("trip_id", SharedPreferencesManager.getInstance(context).tripId);
            jsonObj.addProperty("sequence_number", i+1);
            jsonObj.addProperty ("lat", SharedPreferencesManager.getInstance(context).currentLatitude);
            jsonObj.addProperty ("long", SharedPreferencesManager.getInstance(context).currentLongiitude);
            jsonRoutePoints.add(jsonObj)
        }
        requestRoutesBody.add("route_points", jsonRoutePoints)
        return requestRoutesBody
    }



    private fun callRoutesEverySecond(action : String){
        val ha = Handler()
        ha.postDelayed(object:Runnable {
            public override fun run() {
                if(action.equals("START")){
                    submitRoutes()
                }else{
                    ha.removeCallbacksAndMessages(null);
                }
                ha.postDelayed(this, 1000)
            }
        }, 1000)
    }



    private fun submitResponse(status : String) {
        try {
            val repository = SearchRepositoryProvider.provideMainRepository(AppConstants.API_URL)
            compositeDisposable.add(
                repository.submitCrashResponse(
                    AppConstants.BEARER + SharedPreferencesManager.getInstance(context).authenticationToken
                    ,sendResponseReq(status)).observeOn(
                    AndroidSchedulers.mainThread()
                )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            if (result.success) {
                                //finish()
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


    private fun sendResponseReq(status : String
    ): JsonObject {
        requestBody = JsonObject()
        requestBody.addProperty("id", SharedPreferencesManager.getInstance(context).tripId)
        requestBody.addProperty("end_time", ChTimer.text.toString())
        requestBody.addProperty("end_lat", SharedPreferencesManager.getInstance(context).currentLatitude)
        requestBody.addProperty("end_long", SharedPreferencesManager.getInstance(context).currentLongiitude)
        requestBody.addProperty("trip_status", status)
        requestBody.addProperty("distance",0)
        requestBody.addProperty("duration", 0)
        requestBody.addProperty("avg_speed", 0)
        return requestBody
    }

    override fun onGpsStatusChanged(event: Int) {

    }



}