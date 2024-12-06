package com.zr.nebula.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zr.nebula.item.Log
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<Log>>(listOf())
    val logs = _logs.asStateFlow()

    private var logQuery: RealmResults<Log>? = null

    init {
        viewModelScope.launch {
            val realm = Realm.getDefaultInstance()
            logQuery = realm.where(Log::class.java).findAll()
            _logs.value = realm.copyFromRealm(logQuery!!)

            logQuery?.addChangeListener { t ->
                _logs.value = realm.copyFromRealm(t)
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