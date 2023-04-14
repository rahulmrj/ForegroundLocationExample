package com.example.locationexample2

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
private var _locationLiveData = MutableLiveData<MutableList<Address>>()
    val locationLiveData get() = _locationLiveData

    fun setViewModelLocation(address : MutableList<Address>){
        _locationLiveData.postValue(address)
    }

}