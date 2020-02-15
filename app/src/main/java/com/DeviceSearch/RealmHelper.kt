package com.DeviceSearch

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.DeviceSearch.RealmObjects.BluetoothDevice
import io.realm.Realm
import io.realm.kotlin.where
import java.time.LocalDateTime
import java.util.*

class RealmHelper {
     companion object Instance {
         fun checkDevicesExist(connectedDevice: Array<BluetoothDevice>? = null) {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevices = realm.where<BluetoothDevice>().findAll().size
             var bluetoothDevices = BluetoothHelper.getPairedBluetoothDevices()

             if (storedBluetoothDevices == 0) {
                 // Disable Bluetooth so as to get connection states for initial device setup on re-enable
                 BluetoothAdapter.getDefaultAdapter().disable()

                 /*var locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
             var fusedClient = LocationServices.
             var currentLocation = locationManager.getLastKnownLocation()*/

                 for (bluetoothDevice in bluetoothDevices) {
                     val bluetoothDeviceExists = realm.where<BluetoothDevice>()
                         .equalTo("MacAddress", bluetoothDevice.address).findAll().size

                     if (bluetoothDeviceExists == 0) {
                         createNewDevice(bluetoothDevice)
                     }
                 }

                 BluetoothAdapter.getDefaultAdapter().enable()
             }
         }

         fun upsertDevice (bluetoothDevice: android.bluetooth.BluetoothDevice, connected: Boolean) {
             var realm = Realm.getDefaultInstance()
             var storedBluetoothDevice: BluetoothDevice? = realm.where<BluetoothDevice>()
                 .equalTo("MacAddress", bluetoothDevice.address).findFirst()

             if (storedBluetoothDevice != null) {
                 realm.beginTransaction()
                 storedBluetoothDevice.Connected = connected
                 realm.commitTransaction()
             }
             else {
                 createNewDevice(bluetoothDevice, connected)
             }
         }

         private fun createNewDevice (bluetoothDevice: android.bluetooth.BluetoothDevice,
                               connected: Boolean = false){
             var realm = Realm.getDefaultInstance()

             val newBluetoothDevice = BluetoothDevice(
                 Name = bluetoothDevice.name,
                 MacAddress = bluetoothDevice.address,
                 DeviceType = bluetoothDevice.bluetoothClass.deviceClass,
                 Connected = connected,
                 LastLatitude = -52,
                 LastLongitude = 58
             )

             realm.beginTransaction()
             realm.copyToRealm(newBluetoothDevice)
             realm.commitTransaction()
         }
     }


 }