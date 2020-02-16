package com.DeviceSearch

import android.app.Application
import com.DeviceSearch.Helpers.RealmHelper
import io.realm.Realm
import io.realm.RealmConfiguration
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider


class DeviceSearch: Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("DeviceSearch.realm").build()
        Realm.setDefaultConfiguration(config)

        RealmHelper.checkDevicesExist()

        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build())
    }
}