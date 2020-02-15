package com.DeviceSearch

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        var bluetoothDevice: BluetoothDevice? = null
        if (intent?.action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
            bluetoothDevice = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (bluetoothDevice != null) {
                RealmHelper.upsertDevice(bluetoothDevice, true)
            }
        }
        else if (intent?.action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
            bluetoothDevice = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (bluetoothDevice != null) {
                RealmHelper.upsertDevice(bluetoothDevice, false)
            }
        }
    }
}