package android.thaihn.locationsample.locationservice

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log

class LocationService : Service(), LocationListener {

    companion object {
        private val TAG = LocationService::class.java.simpleName

        private const val LOCATION_INTERVAL: Long = 1000 * 60
        private const val LOCATION_DISTANCE = 10F
    }

    private val mLocationBinder: IBinder = LocationBinder()

    inner class LocationBinder : Binder() {
        val service: LocationService get() = this@LocationService
    }

    var isGPSEnabled = false
    var isNetworkEnabled = false

    private var locationManager: LocationManager? = null

    // Listener to UI
    var listener: LocationListenerEvent? = null

    fun setListenerEvent(_listener: LocationListenerEvent) {
        listener = _listener
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mLocationBinder
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager?.let {
            isGPSEnabled = it.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            // stop service
            listener?.locationChange(null)
            stopSelf()
        } else if (isNetworkEnabled) {
            getLocation(LocationManager.NETWORK_PROVIDER)
        } else if (isGPSEnabled) {
            getLocation(LocationManager.GPS_PROVIDER)
        }
        return START_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        Log.d(TAG, "onLocationChanged: $location ")
        listener?.locationChange(location)
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getLocation(provider: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // request location update
            locationManager?.requestLocationUpdates(
                    provider,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    this
            )

            // get location
            listener?.locationChange(locationManager?.getLastKnownLocation(provider))
        }
    }

    fun stopUpdates() {
        locationManager?.removeUpdates(this)
        stopSelf()
    }
}
