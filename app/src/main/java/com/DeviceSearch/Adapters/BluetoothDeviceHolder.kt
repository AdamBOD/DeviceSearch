package com.DeviceSearch.Adapters

class BluetoothDeviceHolder (val id: String, val name: String,
                             val connected: Boolean, val deviceType: Int) {
    var _id: String
    get() = field

    var _deviceName: String
    get() = field

    var _deviceConnected: Boolean
    get() = field

    var _deviceType: Int
    get() = field


    init {
        _id = id
        _deviceName = name
        _deviceConnected = connected
        _deviceType = deviceType
    }
}