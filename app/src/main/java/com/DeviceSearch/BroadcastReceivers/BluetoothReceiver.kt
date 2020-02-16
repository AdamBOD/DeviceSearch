package com.DeviceSearch.BroadcastReceivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.DeviceSearch.Helpers.RealmHelper
import com.DeviceSearch.Services.LocationService
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager
import com.DeviceSearch.Enums.BroadcastType
import com.DeviceSearch.Services.NotificationService


class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var bluetoothDevice: BluetoothDevice? = intent?.
            getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        var connected: Boolean? = null
        var bluetoothDeviceId: String? = null

        if (bluetoothDevice != null) {
            if (intent?.action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                bluetoothDeviceId = RealmHelper.upsertDevice(bluetoothDevice, true)
                connected = true
            }
            else if (intent?.action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                bluetoothDeviceId = RealmHelper.upsertDevice(bluetoothDevice, false)
                connected = false
            }

            sendBroadcast(BroadcastType.DeviceUpdated, "device-updated",
                bluetoothDeviceId as String, connected as Boolean)

            var shouldNotify: Pair<Boolean, String> = RealmHelper.checkShouldNotify(bluetoothDeviceId)
            if (shouldNotify.first) {
                val serviceIntent = Intent(context, NotificationService::class.java)

                var deviceState = if(connected!!) "Connected" else "Disconnected"
                var deviceName = shouldNotify.second

                serviceIntent.putExtra("title", "Device $deviceState")
                serviceIntent.putExtra("message", "$deviceName has ${deviceState.toLowerCase()}")
                context?.startService(serviceIntent)
            }
        }

        if (!isMyServiceRunning(LocationService::class.java)) {
            val serviceIntent = Intent(context, LocationService::class.java)
            serviceIntent.putExtra("deviceId", bluetoothDeviceId)
            context?.startService(serviceIntent)
        }
        else {
            sendBroadcast(BroadcastType.DeviceLocationRequested, "device-location-requested",
                bluetoothDeviceId as String)
        }
    }

    companion object Instance {
        private var _context: Context? = null
        fun setContext(context: Context?) {
            _context = context
        }

        fun getContext(): Context? {
            return _context as Context
        }

        fun sendBroadcast(broadcastType: BroadcastType, intent: String, id: String, connected: Boolean = false, longitude: Double = 0.0, latitude: Double = 0.0) {
            if (_context != null) {
                val context = _context as Context
                val intent = Intent(intent)
                if (broadcastType == BroadcastType.DeviceUpdated) {
                    if (!RealmHelper.creatingDevices) {
                        intent.putExtra("id", id)
                        intent.putExtra("connected", connected)
                    }
                }
                else if (broadcastType == BroadcastType.DeviceLocationRequested) {
                    intent.putExtra("deviceId", id)
                }
                else if (broadcastType == BroadcastType.DeviceLocationUpdated) {
                    intent.putExtra("deviceId", id)
                    intent.putExtra("longitude", longitude)
                    intent.putExtra("latitude", latitude)
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = _context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}