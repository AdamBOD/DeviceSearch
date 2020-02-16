package com.DeviceSearch.Helpers

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import com.DeviceSearch.BroadcastReceivers.BluetoothReceiver
import com.DeviceSearch.RealmObjects.BluetoothDevice
import com.DeviceSearch.Services.LocationService
import com.DeviceSearch.Services.NotificationService
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class RealmHelper {
     companion object Instance {
         var creatingDevices: Boolean = false

         fun checkDevicesExist(connectedDevice: Array<BluetoothDevice>? = null) {
             creatingDevices = true
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevices = realm.where<BluetoothDevice>().findAll().size
             var bluetoothDevices =
                 BluetoothHelper.getPairedBluetoothDevices()

             if (storedBluetoothDevices == 0) {
                 // Disable Bluetooth so as to get connection states for initial device setup on re-enable
                 BluetoothAdapter.getDefaultAdapter().disable()

                 for (bluetoothDevice in bluetoothDevices) {
                     val bluetoothDeviceExists = realm.where<BluetoothDevice>()
                         .equalTo("MacAddress", bluetoothDevice.address).findAll().size

                     if (bluetoothDeviceExists == 0) {
                         createNewDevice(
                             bluetoothDevice
                         )
                     }
                 }

                 BluetoothAdapter.getDefaultAdapter().enable()
             }
             creatingDevices = false
         }

         fun upsertDevice (bluetoothDevice: android.bluetooth.BluetoothDevice, connected: Boolean)
             :String {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevice: BluetoothDevice? = realm.where<BluetoothDevice>()
                 .equalTo("MacAddress", bluetoothDevice.address).findFirst()

             if (storedBluetoothDevice != null) {
                 realm.beginTransaction()
                 storedBluetoothDevice.Connected = connected
                 storedBluetoothDevice.LastUpdatedOn = Calendar.getInstance().time
                 realm.commitTransaction()

                 return storedBluetoothDevice.Id
             }
             else {
                 return createNewDevice(
                     bluetoothDevice,
                     connected
                 )
             }
         }

         fun updateDeviceLocation (deviceId: String, longitude: Double, latitude: Double) {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevice: BluetoothDevice? = realm.where<BluetoothDevice>()
                 .equalTo("Id", deviceId).findFirst()

             if (storedBluetoothDevice != null) {
                 realm.beginTransaction()
                 storedBluetoothDevice.LastLongitude = longitude
                 storedBluetoothDevice.LastLatitude = latitude
                 realm.commitTransaction()

             }
         }

         fun updateDeviceNotificationSetting (deviceId: String, notifyOfChanges: Boolean) {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevice: BluetoothDevice? = realm.where<BluetoothDevice>()
                 .equalTo("Id", deviceId).findFirst()

             if (storedBluetoothDevice != null) {
                 realm.beginTransaction()
                 storedBluetoothDevice.NotifyOnConnectionChange = notifyOfChanges
                 realm.commitTransaction()
             }
         }

         fun checkShouldNotify (deviceId: String): Pair<Boolean, String> {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevice: BluetoothDevice? = realm.where<BluetoothDevice>()
                 .equalTo("Id", deviceId).findFirst()

             if (storedBluetoothDevice != null) {
                 return Pair<Boolean, String>(storedBluetoothDevice.NotifyOnConnectionChange!!, storedBluetoothDevice.Name)
             }

             return Pair<Boolean, String>(false, "")
         }

         private fun createNewDevice (bluetoothDevice: android.bluetooth.BluetoothDevice,
                               connected: Boolean = false): String {
             var realm = Realm.getDefaultInstance()

             val newBluetoothDevice = BluetoothDevice(
                 Name = bluetoothDevice.name,
                 MacAddress = bluetoothDevice.address,
                 DeviceType = bluetoothDevice.bluetoothClass.deviceClass,
                 Connected = connected,
                 LastLatitude = 0.0,
                 LastLongitude = 0.0
             )

             realm.beginTransaction()
             realm.copyToRealm(newBluetoothDevice)
             realm.commitTransaction()

             return newBluetoothDevice.Id
         }
     }
 }