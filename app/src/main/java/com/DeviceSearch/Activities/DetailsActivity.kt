package com.DeviceSearch.Activities

import android.bluetooth.BluetoothClass
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.DeviceSearch.Helpers.RealmHelper
import com.DeviceSearch.R
import com.DeviceSearch.RealmObjects.BluetoothDevice
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.where
import com.google.android.gms.maps.CameraUpdateFactory
import java.text.SimpleDateFormat
import java.util.*


class DetailsActivity: AppCompatActivity(), OnMapReadyCallback{
    private lateinit var _deviceId: String

    private lateinit var _toolBar: Toolbar

    private var _device: BluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    override fun onResume() {
        super.onResume()
        var intent: Intent = getIntent()
        _deviceId = intent.getStringExtra("deviceId")

        setupViews()

        getData()
    }

    private fun setupViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getData() {
        var realm = Realm.getDefaultInstance()
        _device = realm.where<BluetoothDevice>().equalTo("Id", _deviceId).findFirst()

        if (_device != null) {
            populateData()
        }
    }

    private fun populateData() {
        supportActionBar?.title = _device?.Name

        val deviceNameTextView = findViewById<TextView>(R.id.device_name_textview)
        val deviceIcon = findViewById<ImageView>(R.id.device_icon)
        val deviceConnectedTextView = findViewById<TextView>(R.id.device_connected_textview)
        val deviceAddressTextView = findViewById<TextView>(R.id.device_address_textview)
        val deviceLastUpdatedTextView = findViewById<TextView>(R.id.device_last_updated_textview)
        val copyAddressButton = findViewById<ImageButton>(R.id.copy_address_button)
        val deviceNotifySwitch = findViewById<Switch>(R.id.notification_switch)

        deviceNameTextView.text = _device?.Name

        setIcon(deviceIcon, _device?.DeviceType as Int)

        if (_device?.Connected as Boolean) {
            deviceConnectedTextView.setTextColor(applicationContext.resources.getColor(R.color.colorConnected))
        }
        else {
            deviceConnectedTextView.setTextColor(applicationContext.resources.getColor(R.color.colorDisconnected))
        }
        deviceConnectedTextView.text = if(_device?.Connected!!) "Connected" else "Disconnected"

        copyAddressButton.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData = ClipData.newPlainText(_device?.Name, _device?.MacAddress)
            clipboard.setPrimaryClip(clipData)

            Toast.makeText(
                applicationContext,
                "Address for ${_device?.Name} copied to clipboard",
                Toast.LENGTH_LONG).show()
        }

        deviceAddressTextView.text = _device?.MacAddress

        val pattern = "dd/MM/yyyy hh:mm"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(_device?.LastUpdatedOn as Date)
        deviceLastUpdatedTextView.text = date

        deviceNotifySwitch.isChecked = _device?.NotifyOnConnectionChange as Boolean
        deviceNotifySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            RealmHelper.updateDeviceNotificationSetting(_deviceId, isChecked)
        }

        val mMapFragment = MapFragment.newInstance()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.map_container, mMapFragment)
        fragmentTransaction.commit()

        mMapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        if (_device?.LastLatitude != 0.0 && _device?.LastLongitude != 0.0) {
            var position = LatLng(_device?.LastLatitude as Double,
                _device?.LastLongitude as Double)
            map?.addMarker(MarkerOptions()
                .position(position))

            val update = CameraUpdateFactory.newLatLngZoom(position, 12.5F)
            map?.moveCamera(update)
        }
    }

    fun setIcon(iconView: ImageView, deviceType: Int) {
        if (deviceType == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE) {
            iconView.setImageResource(R.drawable.ic_directions_car_black_35dp)
        }
        else if (deviceType == BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO ||
            deviceType == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER ||
            deviceType == BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED) {
            iconView.setImageResource(R.drawable.ic_speaker_black_35dp)
        }
        else if (deviceType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
            iconView.setImageResource(R.drawable.ic_headset_black_35dp)
        }
        else if (deviceType == BluetoothClass.Device.WEARABLE_WRIST_WATCH) {
            iconView.setImageResource(R.drawable.ic_watch_black_35dp)
        }
        else {
            iconView.setImageResource(R.drawable.ic_bluetooth_black_35dp)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}