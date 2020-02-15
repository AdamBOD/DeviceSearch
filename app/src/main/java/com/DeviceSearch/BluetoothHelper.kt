package com.DeviceSearch

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context.BLUETOOTH_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat


class BluetoothHelper {
    companion object Instance {
        fun getPairedBluetoothDevices(): Array<BluetoothDevice> {
            var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            var bluetoothDevices = mBluetoothAdapter.bondedDevices

            return bluetoothDevices.toTypedArray()
        }
    }
}