package android.thaihn.locationsample.googleplayservice

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.thaihn.locationsample.R
import android.thaihn.locationsample.databinding.ActivityGoogleApisBinding
import android.util.Log
import android.widget.Toast


class GoogleAPIsActivity : AppCompatActivity() {

    companion object {
        private val TAG = GoogleAPIsActivity::class.java.simpleName

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    private var googleAPIsService: GoogleAPIsService? = null
    private var mBoundService = false

    private var myReceiver: MyReceiver? = null

    private lateinit var googleApisBinding: ActivityGoogleApisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleApisBinding = DataBindingUtil.setContentView(this, R.layout.activity_google_apis)

        myReceiver = MyReceiver()

        if (!checkPermissions()) {
            requestPermissions()
        }

        googleApisBinding.btnGetLocation.setOnClickListener {
            if (!checkPermissions()) {
                requestPermissions()
            } else {
                googleAPIsService?.requestLocationUpdates()
            }
        }
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as GoogleAPIsService.GoogleApiBinder
            googleAPIsService = binder.service

            mBoundService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            googleAPIsService = null
            mBoundService = false
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, GoogleAPIsService::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBoundService) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mConnection)
            mBoundService = false
        }
    }

    override fun onResume() {
        super.onResume()
        myReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it,
                    IntentFilter(GoogleAPIsService.ACTION_BROADCAST_LOCATION))
        }
    }

    override fun onPause() {
        myReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            AlertDialog.Builder(this)
                    .setTitle("Need locaiton permission")
                    .setMessage("We need access your location permission")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(this@GoogleAPIsActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    })
                    .create()
                    .show()
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@GoogleAPIsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission was granted.
                    googleAPIsService?.requestLocationUpdates()
                else -> {
                    // Permission denied.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra(GoogleAPIsService.EXTRA_LATITUDE, 0.0)
            val longitude = intent.getDoubleExtra(GoogleAPIsService.EXTRA_LONGITUDE, 0.0)
            googleApisBinding.tvMyLocation.text = "Location: latitude:$latitude-longitude:$longitude"
        }
    }
}
