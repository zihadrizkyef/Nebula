package com.zr.nebula.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zr.repository.DbHelper
import com.zr.repository.item.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _logs = MutableLiveData<List<Log>>(listOf())
    val logs: LiveData<List<Log>> get() = _logs

    init {
        viewModelScope.launch {
            _logs.value = DbHelper.getAll()
            DbHelper.setLogsListener {
                _logs.value = it
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            DbHelper.deleteAll()
        }
    }
}