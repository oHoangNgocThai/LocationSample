package android.thaihn.locationsample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.locationsample.databinding.ActivityMainBinding
import android.thaihn.locationsample.geocoding.GeocodingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.btnGeocoding.setOnClickListener {
            startActivity(Intent(this, GeocodingActivity::class.java))
        }
    }
}
