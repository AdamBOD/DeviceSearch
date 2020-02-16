package com.DeviceSearch.Services

import android.app.IntentService
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import com.DeviceSearch.Helpers.RealmHelper


class LocationService: IntentService("LocationService") {
    var devicesToUpdate: ArrayList<String> = arrayListOf()

    override fun onHandleIntent(intent: Intent?) {
        devicesToUpdate.add(intent?.getStringExtra("deviceId") as String)

        var locationManager: LocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        if (applicationContext.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            var locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    var latitude = location!!.latitude
                    var longitude = location!!.longitude

                    for (deviceToUpdate in devicesToUpdate) {
                        RealmHelper.updateDeviceLocation(deviceToUpdate, longitude, latitude)
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }



            }

            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }
        else {
            Log.e("Invalid Permission", "Missing Location permissions")
        }
    }
}