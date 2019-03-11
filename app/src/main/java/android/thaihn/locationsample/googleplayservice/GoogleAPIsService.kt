package android.thaihn.locationsample.googleplayservice

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.*


class GoogleAPIsService : Service() {

    companion object {
        private val TAG = GoogleAPIsService::class.java.simpleName

        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        val ACTION_BROADCAST_LOCATION = GoogleAPIsService::class.java.name + "LocationBroadcast"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }

    private var mLocationRequest = LocationRequest()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocation: Location? = null

    private val mGoogleApiBinder: IBinder = GoogleApiBinder()

    inner class GoogleApiBinder : Binder() {
        val service: GoogleAPIsService get() = this@GoogleAPIsService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mGoogleApiBinder
    }

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult!!.lastLocation)
            }
        }

        createLocationRequest()
        getLastLocation()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        startService(Intent(applicationContext, GoogleAPIsService::class.java))
        try {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient?.lastLocation
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            mLocation = task.result
                            Log.d(TAG, "getLastLocation: $mLocation")
                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        mLocation = location
        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST_LOCATION)
        intent.putExtra(EXTRA_LATITUDE, location.latitude)
        intent.putExtra(EXTRA_LONGITUDE, location.longitude)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}
