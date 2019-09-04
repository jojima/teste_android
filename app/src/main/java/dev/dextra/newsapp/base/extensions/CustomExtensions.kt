package dev.dextra.newsapp.base.extensions

import androidx.lifecycle.MutableLiveData

//add all the values from the list to the LiveData
fun <T> MutableLiveData<ArrayList<T>>.addListValues(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.postValue(value)
}

//dev.dextra.newsapp.base.extensions.clear the LiveData
fun <T> MutableLiveData<ArrayList<T>>.clear() {
    this.postValue(null)
}