package com.example.devicesearch

import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.*
import android.widget.ListView
import android.widget.Toast
import com.example.classes.BluetoothDeviceHolder

class MainActivity : AppCompatActivity() {

    private var _bluetoothDevices: Array<BluetoothDeviceHolder> = arrayOf<BluetoothDeviceHolder>()
    private lateinit var _listView: ListView
    private lateinit var _adapter: BluetoothDeviceAdapter
    private lateinit var _appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _appContext = this

        setupViews()
        addEventHandlers()
    }

    private fun setupViews () {
        _listView = findViewById(R.id.devices_list_view)
    }

    private fun addEventHandlers () {
        _listView.onItemLongClickListener = object: OnItemLongClickListener {
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
        }
    }

    override fun onResume() {
        super.onResume()

        _adapter = BluetoothDeviceAdapter(this, getBluetoothDevices())
        _listView.adapter = _adapter
    }

    private fun getBluetoothDevices (): Array<BluetoothDeviceHolder> {
        var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        _bluetoothDevices = arrayOf<BluetoothDeviceHolder>()
        var bluetoothDevices = mBluetoothAdapter.bondedDevices
        for (bluetoothDevice in bluetoothDevices) {
            _bluetoothDevices += BluetoothDeviceHolder(bluetoothDevice.name, bluetoothDevice.address)
        }
        return _bluetoothDevices
    }
}
