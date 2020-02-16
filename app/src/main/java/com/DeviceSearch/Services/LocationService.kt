package com.DeviceSearch.Services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.DeviceSearch.Helpers.RealmHelper
import android.os.Binder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.Exception


class LocationService: Service() {

    private val mBinder = LocalBinder()
    var devicesToUpdate: ArrayList<String> = arrayListOf()

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        devicesToUpdate.add(intent?.getStringExtra("deviceId") as String)
        getLocation()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            IntentFilter("device-location-requested")
        )

        return super.onStartCommand(intent, flags, startId)
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            devicesToUpdate.add(intent?.getStringExtra("deviceId") as String)
            getLocation()
        }
    }

    private fun getLocation() {

        var locationManager: LocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        if (applicationContext.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            var locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    var latitude = location!!.latitude
                    var longitude = location!!.longitude

                    try {
                        var index = 0
                        for (deviceToUpdate in devicesToUpdate) {
                            RealmHelper.updateDeviceLocation(deviceToUpdate, longitude, latitude)
                            devicesToUpdate.removeAt(index)
                            index ++
                        }

                        if (devicesToUpdate.size == 0) {
                            stopSelf()
                        }
                    }
                    catch (e: Exception) {
                        if (e.message != null) {
                            Log.e("Error", e.message)
                        }
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
            stopSelf()
        }
    }
}