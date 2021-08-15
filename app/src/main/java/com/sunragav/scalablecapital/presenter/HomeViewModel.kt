package com.sunragav.scalablecapital.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {
    val title = MutableLiveData<String>()
}