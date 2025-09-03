package com.zr.nebula.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zr.nebula.data.DbHelper
import com.zr.nebula.data.item.Log
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {
    private val _logs = MutableLiveData<List<Log>>(listOf())
    val logs: LiveData<List<Log>> get() = _logs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            _logs.value = DbHelper.getAll()
            DbHelper.setLogsListener {
                if (isLoading.value == true) _isLoading.value = false
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