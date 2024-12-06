package com.zr.nebula.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zr.nebula.item.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<Log>>(listOf())
    val logs = _logs.asStateFlow()

    private var logQuery: RealmResults<Log>? = null
    private var lastListenerJob: Job? = null
    private var lastListenerTime = 0L

    init {
        viewModelScope.launch {
            val realm = Realm.getDefaultInstance()
            logQuery = realm.where(Log::class.java)
                .sort("createdAt", Sort.DESCENDING)
                .findAll()
            _logs.value = realm.copyFromRealm(logQuery!!)

            logQuery?.addChangeListener { t ->
                if (System.currentTimeMillis() - lastListenerTime > 500) {
                    lastListenerTime = System.currentTimeMillis()
                    _logs.value = realm.copyFromRealm(t)
                } else {
                    lastListenerJob?.cancel()
                    lastListenerJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        _logs.value = realm.copyFromRealm(t)
                    }
                }
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction {
                    it.deleteAll()
                }
            }
        }
    }
}