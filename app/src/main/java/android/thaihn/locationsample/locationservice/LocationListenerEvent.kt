package android.thaihn.locationsample.locationservice

import android.location.Location

interface LocationListenerEvent {
    fun locationChange(location: Location?)
}
