package com.DeviceSearch.Helpers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice


class BluetoothHelper {
    companion object Instance {
        fun getPairedBluetoothDevices(): Array<BluetoothDevice> {
            var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            var bluetoothDevices = mBluetoothAdapter.bondedDevices

            return bluetoothDevices.toTypedArray()
        }
    }
}