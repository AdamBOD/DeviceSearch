package com.DeviceSearch.RealmObjects

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*

open class BluetoothDevice (
    @PrimaryKey var Id: String = UUID.randomUUID().toString(),
    var Name: String = "",
    @Index var MacAddress: String = "",
    var DeviceType: Int = 0,
    var Connected: Boolean = false,
    var LastLongitude: Long = 0L,
    var LastLatitude: Long = 0L,
    var NotifyOnConnectionChange: Boolean = false,
    var LastUpdatedOn: Date = Calendar.getInstance().time
): RealmObject()