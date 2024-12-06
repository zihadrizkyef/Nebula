package com.zr.nebula.helper

import android.content.Context
import com.zr.nebula.item.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object DbHelper {
    private val realmScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        Realm.init(context)
        val configuration = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(configuration)
    }

    fun insert(log: Log) = realmScope.launch {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.insert(log)
            }
        }
    }
}