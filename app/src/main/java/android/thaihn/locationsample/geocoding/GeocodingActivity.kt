package android.thaihn.locationsample.geocoding

import android.databinding.DataBindingUtil
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.locationsample.R
import android.thaihn.locationsample.databinding.ActivityGeocodingBinding
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class GeocodingActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    companion object {
        private const val TAG = "GeocodingActivity"
    }

    private lateinit var mMap: GoogleMap
    private var mMarker: Marker? = null

    private lateinit var geocodingBinding: ActivityGeocodingBinding

    private val defaultLocation = LatLng(21.017320501362633, 105.78358800000001)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocodingBinding = DataBindingUtil.setContentView(this, R.layout.activity_geocoding)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocodingBinding.btnFind.setOnClickListener {
            val name = geocodingBinding.edtInput.text.toString().trim()
            val result = getLocationFromName(name)
            geocodingBinding.tvResult.text = result
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener(this)

        addMarker(defaultLocation, mMap)
    }

    override fun onMapClick(latLng: LatLng?) {
        latLng?.let {
            addMarker(latLng, mMap)
            val address = getAddressFromLocation(latLng.latitude, latLng.longitude)
            geocodingBinding.tvResult.text = address
        }
    }

    private fun addMarker(latLng: LatLng, googleMap: GoogleMap) {
        mMarker?.remove()

        val markerOptions = MarkerOptions().position(latLng)
                .snippet("Lat: ${latLng.latitude} - Long: ${latLng.longitude}")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .title("I'm here")

        mMarker = googleMap.addMarker(markerOptions)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
    }

    // Get address from latitude and longitude
    private fun getAddressFromLocation(latitude: Double, longitude: Double): StringBuilder {
        val geocoder = Geocoder(this)
        val results = StringBuilder()
        val maxResult = 10

        val listAddress = geocoder.getFromLocation(latitude, longitude, maxResult)

        if (listAddress.size > 0) {
            listAddress.forEachIndexed { index, address ->
                val strAddress = StringBuilder()
                Log.d(TAG, "address: $address")
                strAddress.append("${address.featureName},${address.countryName},${address.adminArea}")
                results.append("---").append(strAddress).append(System.getProperty("line.separator"))
            }
        }
        return results
    }

    private fun getLocationFromName(name: String): StringBuilder {
        val geocoder = Geocoder(this)
        val maxResult = 10
        val listAddress = geocoder.getFromLocationName(name, maxResult)

        val results = StringBuilder()

        if (listAddress.size > 0) {
            listAddress.forEachIndexed { index, address ->
                val strAddress = StringBuilder()
                Log.d(TAG, "address: $address")
                strAddress.append("${address.featureName},${address.countryName},${address.adminArea}")
                results.append("---").append(strAddress).append(System.getProperty("line.separator"))
                // update map
                val targetLocation = LatLng(address.latitude, address.longitude)
                addMarker(targetLocation, mMap)
            }
        }
        return results
    }
}