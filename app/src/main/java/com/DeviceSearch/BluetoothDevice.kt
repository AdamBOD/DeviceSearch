package com.DeviceSearch

class BluetoothDeviceHolder (val name: String, val address: String) {
    var _deviceName: String
    get() = field

    var _deviceAddress: String
    get() = field


    init {
        _deviceName = name
        _deviceAddress = address
    }
}