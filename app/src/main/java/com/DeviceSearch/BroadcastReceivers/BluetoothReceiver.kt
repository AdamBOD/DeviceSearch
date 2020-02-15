package com.DeviceSearch.BroadcastReceivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.DeviceSearch.Helpers.RealmHelper

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        var bluetoothDevice: BluetoothDevice? = intent?.
            getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
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

            sendBroadcast("device-updated", bluetoothDeviceId as String, connected as Boolean)
        }
    }

    companion object Instance {
        private var _context: Context? = null
        fun setContext (context: Context?) {
            _context = context
        }
    }

    private fun sendBroadcast(intent: String, id: String, connected: Boolean) {
        if (_context != null && RealmHelper.creatingDevices == false) {
            val context = _context as Context
            val intent = Intent(intent)

            intent.putExtra("id", id)
            intent.putExtra("connected", connected)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

    }
}