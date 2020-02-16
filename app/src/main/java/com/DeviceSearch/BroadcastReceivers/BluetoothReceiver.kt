package com.DeviceSearch.BroadcastReceivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.DeviceSearch.Helpers.RealmHelper
import com.DeviceSearch.Services.LocationService

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

            sendBroadcast("device-updated", bluetoothDeviceId as String, connected as Boolean)
        }

        val serviceIntent = Intent(context, LocationService::class.java)
        serviceIntent.putExtra("deviceId", bluetoothDeviceId)
        context?.startService(serviceIntent)
    }

    companion object Instance {
        private var _context: Context? = null
        fun setContext (context: Context?) {
            _context = context
        }
    }

    private fun sendBroadcast(intent: String, id: String, connected: Boolean) {
        if (_context != null && !RealmHelper.creatingDevices) {
            val context = _context as Context
            val intent = Intent(intent)

            intent.putExtra("id", id)
            intent.putExtra("connected", connected)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

    }

    private fun getLocation() {

    }
}