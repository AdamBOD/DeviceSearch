package com.example.devicesearch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.classes.BluetoothDeviceHolder

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

        val titleTextView = rowView.findViewById(R.id.list_item_title) as TextView
        val subtitleTextView = rowView.findViewById(R.id.list_item_subtitle) as TextView

        val bluetoothDevice = getItem(position) as BluetoothDeviceHolder

        titleTextView.text = bluetoothDevice._deviceName
        subtitleTextView.text = bluetoothDevice._deviceAddress

        return rowView
    }
}