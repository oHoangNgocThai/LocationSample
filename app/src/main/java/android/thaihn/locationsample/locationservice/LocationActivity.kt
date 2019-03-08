package android.thaihn.locationsample.locationservice

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.thaihn.locationsample.R
import android.thaihn.locationsample.databinding.ActivityLocationBinding
import android.util.Log


class LocationActivity : AppCompatActivity(), LocationListenerEvent {

    companion object {
        private val TAG = LocationActivity::class.java.simpleName

        private const val PERMISSION_REQUEST_LOCATION = 99
    }

    private var locationService: LocationService? = null
    private var mBoundService = false

    private lateinit var locationBinding: ActivityLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationBinding = DataBindingUtil.setContentView(this, R.layout.activity_location)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocationBinder
            locationService = binder.service
            locationService?.setListenerEvent(this@LocationActivity)
            mBoundService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            mBoundService = false
        }
    }

    override fun locationChange(location: Location?) {
        Log.d(TAG, "locationChange: location:$location")
        location?.let {
            locationBinding.tvMyLocation.text = "My Location: ${it.latitude} - ${it.longitude}"
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermission()) {
            Intent(this, LocationService::class.java).also {
                startService(it)
                bindService(it, mConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBoundService) {
            unbindService(mConnection)
            mBoundService = false
        }
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                        .setTitle("Need locaiton permission")
                        .setMessage("We need access your location permission")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this@LocationActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    PERMISSION_REQUEST_LOCATION)
                        })
                        .create()
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                        PERMISSION_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }
}
