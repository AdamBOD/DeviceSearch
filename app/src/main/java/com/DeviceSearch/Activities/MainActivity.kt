package com.DeviceSearch.Activities

import android.bluetooth.BluetoothClass
import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.DeviceSearch.Adapters.BluetoothDeviceAdapter
import com.DeviceSearch.Adapters.BluetoothDeviceHolder
import com.DeviceSearch.BroadcastReceivers.BluetoothReceiver
import com.DeviceSearch.R
import com.DeviceSearch.RealmObjects.BluetoothDevice
import io.realm.Realm
import io.realm.kotlin.where

class MainActivity : AppCompatActivity() {
    private lateinit var _listView: ListView
    private lateinit var _adapter: BluetoothDeviceAdapter
    private lateinit var _appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _appContext = this
        setupViews()
        addEventHandlers()

        BluetoothReceiver.setContext(this)

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            IntentFilter("device-updated")
        )
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val id = intent.getStringExtra("id")
            val connected = intent.getStringExtra("connected")

            _adapter.updateRow(id)

            _adapter =
                BluetoothDeviceAdapter(_appContext, getBluetoothDevices())
            _listView.adapter = _adapter
        }
    }

    override fun onResume() {
        super.onResume()

        _adapter = BluetoothDeviceAdapter(this, getBluetoothDevices())
        _listView.adapter = _adapter
    }

    private fun setupViews () {
        _listView = findViewById(R.id.devices_list_view)
    }

    private fun addEventHandlers () {
       /* _listView.onItemLongClickListener = object: OnItemLongClickListener {
            override fun onItemLongClick(v: AdapterView<*>, arg1: View, pos: Int, id: Long): Boolean {
                val device: BluetoothDeviceHolder = _adapter.getItem(pos)
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData = ClipData.newPlainText(device._deviceName, device._deviceAddress)
                clipboard.setPrimaryClip(clipData)

                Toast.makeText(
                    _appContext,
                    "Address for ${device._deviceName} copied to clipboard",
                    Toast.LENGTH_LONG).show()
                return true
            }
        }*/
    }

    private fun getBluetoothDevices(): Array<BluetoothDeviceHolder> {
        var realm = Realm.getDefaultInstance()
        var storedBluetoothDevices = realm.where<BluetoothDevice>().findAll()
        var bluetoothDevices: Array<BluetoothDeviceHolder> = arrayOf()

        for (bluetoothDevice in storedBluetoothDevices) {
            bluetoothDevices += BluetoothDeviceHolder(
                bluetoothDevice.Id,
                bluetoothDevice.Name,
                bluetoothDevice.Connected,
                bluetoothDevice.DeviceType
            )
        }

        return bluetoothDevices
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }
}
