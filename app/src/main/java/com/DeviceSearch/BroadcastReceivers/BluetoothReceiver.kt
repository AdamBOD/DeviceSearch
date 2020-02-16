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


class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var bluetoothDevice: BluetoothDevice? = intent?.
            getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        var connected: Boolean? = null
        var bluetoothDeviceId: String? = null

        if (bluetoothDevice != null) {
            if (intent?.action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                getLocation()
                bluetoothDeviceId = RealmHelper.upsertDevice(bluetoothDevice, true)
                connected = true
            }
            else if (intent?.action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                getLocation()
                bluetoothDeviceId = RealmHelper.upsertDevice(bluetoothDevice, false)
                connected = false
            }

            sendBroadcast(BroadcastType.DeviceUpdated, "device-updated",
                bluetoothDeviceId as String, connected as Boolean)
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
        fun setContext (context: Context?) {
            _context = context
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

    private fun sendBroadcast(broadcastType: BroadcastType, intent: String, id: String, connected: Boolean = false) {
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

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    private fun getLocation() {

    }
}