package com.DeviceSearch.Activities

import android.bluetooth.BluetoothClass
import android.content.*
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.DeviceSearch.Adapters.BluetoothDeviceAdapter
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

    private lateinit var _deviceNameTextView: TextView
    private lateinit var _deviceIcon: ImageView
    private lateinit var _deviceConnectedTextView: TextView
    private lateinit var _deviceAddressTextView: TextView
    private lateinit var _deviceLastUpdatedTextView: TextView

    private lateinit var _map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    override fun onResume() {
        super.onResume()
        var intent: Intent = getIntent()
        _deviceId = intent.getStringExtra("deviceId")

        LocalBroadcastManager.getInstance(this).registerReceiver(deviceUpdatedReceiver,
            IntentFilter("device-updated")
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(deviceLocationUpdatedReceiver,
            IntentFilter("device-location-updated")
        )

        setupViews()

        getData()
    }

    private val deviceUpdatedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getStringExtra("id")

            if (id == _device?.Id) {
                _device = getObject(id)
                setupConnectedTextView()
                setupLastUpdatedOnTextView(_device!!.LastUpdatedOn)
            }
        }
    }

    private val deviceLocationUpdatedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getStringExtra("deviceId")

            if (id == _device?.Id && _map != null) {
                _device = getObject(id)
                getData()
            }
        }
    }

    private fun setupViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getData() {
        _device = getObject(_deviceId)

            if (_device != null) {
            populateData()
        }
    }

    private fun getObject(id: String): BluetoothDevice? {
        var realm = Realm.getDefaultInstance()
        var device = realm.where<BluetoothDevice>().equalTo("Id", id).findFirst()
        return device
    }

    private fun populateData() {
        supportActionBar?.title = _device?.Name

        _deviceNameTextView = findViewById<TextView>(R.id.device_name_textview)
        _deviceIcon = findViewById<ImageView>(R.id.device_icon)
        _deviceConnectedTextView = findViewById<TextView>(R.id.device_connected_textview)
        _deviceAddressTextView = findViewById<TextView>(R.id.device_address_textview)
        _deviceLastUpdatedTextView = findViewById<TextView>(R.id.device_last_updated_textview)
        val copyAddressButton = findViewById<ImageButton>(R.id.copy_address_button)
        val deviceNotifySwitch = findViewById<Switch>(R.id.notification_switch)

        _deviceNameTextView.text = _device?.Name

        setIcon(_deviceIcon, _device?.DeviceType as Int)

        setupConnectedTextView()

        copyAddressButton.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData = ClipData.newPlainText(_device?.Name, _device?.MacAddress)
            clipboard.setPrimaryClip(clipData)

            Toast.makeText(
                applicationContext,
                "Address for ${_device?.Name} copied to clipboard",
                Toast.LENGTH_LONG).show()
        }

        _deviceAddressTextView.text = _device?.MacAddress

        setupLastUpdatedOnTextView(_device?.LastUpdatedOn!!)

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

    private fun setupConnectedTextView() {
        if (_device?.Connected as Boolean) {
            _deviceConnectedTextView.setTextColor(applicationContext.resources.getColor(R.color.colorConnected))
        }
        else {
            _deviceConnectedTextView.setTextColor(applicationContext.resources.getColor(R.color.colorDisconnected))
        }
        _deviceConnectedTextView.text = if(_device?.Connected!!) "Connected" else "Disconnected"
    }

    private fun setupLastUpdatedOnTextView(lastUpdatedOn: Date) {
        val pattern = "dd/MM/yyyy hh:mm"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(lastUpdatedOn)
        _deviceLastUpdatedTextView.text = date
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            _map = map!!
            setupMap()
        }
    }

    private fun setupMap() {
        if (_device?.LastLatitude != 0.0 && _device?.LastLongitude != 0.0) {
            var position = LatLng(_device?.LastLatitude as Double,
                _device?.LastLongitude as Double)

            _map.addMarker(MarkerOptions()
                .position(position))

            val update = CameraUpdateFactory.newLatLngZoom(position, 12.5F)
            _map.moveCamera(update)
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