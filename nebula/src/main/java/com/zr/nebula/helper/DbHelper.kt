package com.zr.nebula.helper

import android.content.Context
import com.zr.nebula.item.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort
import io.realm.annotations.RealmModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DbHelper {
    @RealmModule(library = true, allClasses = true)
    private class NebulaRealmModule

    private val realmScope = CoroutineScope(Dispatchers.IO)
    private lateinit var realm: Realm
    private var logQuery: RealmResults<Log>? = null

    fun init(context: Context) {
        Realm.init(context)
        val configuration = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .modules(NebulaRealmModule())
            .build()
        realm = Realm.getInstance(configuration)
    }

    fun insert(log: Log) = realmScope.launch {
        realm.executeTransaction {
            it.insert(log)
        }
    }

    fun getLogs(): List<Log> {
        val logs = realm.where(Log::class.java).findAll()
        return realm.copyFromRealm(logs)
    }

    fun setLogsListener(throttleTime: Long = 500, onChanged: (List<Log>) -> Unit) {
        logQuery = realm.where(Log::class.java)
            .sort("createdAt", Sort.DESCENDING)
            .findAll()

        var lastListenerTime = 0L
        var lastListenerJob: Job? = null
        val mainScope = CoroutineScope(Dispatchers.Main)
        logQuery?.addChangeListener { t ->
            if (System.currentTimeMillis() - lastListenerTime > throttleTime) {
                lastListenerTime = System.currentTimeMillis()
                onChanged(realm.copyFromRealm(t))
            } else {
                lastListenerJob?.cancel()
                lastListenerJob = mainScope.launch {
                    delay(500)
                    onChanged(realm.copyFromRealm(t))
                }
            }
        }
    }
}