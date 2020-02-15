package com.DeviceSearch.Adapters

import android.bluetooth.BluetoothClass
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.DeviceSearch.R

class BluetoothDeviceAdapter(private val context: Context,
                             private val dataSource: Array<BluetoothDeviceHolder>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): BluetoothDeviceHolder {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_bluetooth_device, parent, false)

        val iconView = rowView.findViewById(R.id.list_item_icon) as ImageView
        val titleTextView = rowView.findViewById(R.id.list_item_title) as TextView
        val subtitleTextView = rowView.findViewById(R.id.list_item_subtitle) as TextView

        val bluetoothDevice = getItem(position) as BluetoothDeviceHolder

        titleTextView.text = bluetoothDevice._deviceName

        if (bluetoothDevice._deviceConnected) {
            subtitleTextView.setTextColor(context.resources.getColor(R.color.colorConnected))
        }
        else {
            subtitleTextView.setTextColor(context.resources.getColor(R.color.colorDisconnected))
        }

        setIcon(iconView, bluetoothDevice._deviceType)

        subtitleTextView.text = if(bluetoothDevice._deviceConnected) "Connected" else "Disconnected"

        return rowView
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

    fun updateRow(id: String) {
        val row: BluetoothDeviceHolder? = dataSource.find {
            it._id == id
        }
    }
}