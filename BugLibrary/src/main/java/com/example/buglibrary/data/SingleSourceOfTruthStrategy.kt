package com.example.buglibrary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

fun <T, A> resultLiveData(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Result<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Result<T>> =
    liveData(Dispatchers.IO) {
        emit(Result.loading<T>())

        val source = databaseQuery.invoke().map { Result.success(it) }
        Log.d("TAG", "resultLiveData: ${source.value?.status}")
        emitSource(source)

        val responseStatus = networkCall.invoke()

        if (responseStatus.status == Result.Status.SUCCESS) {
            saveCallResult(responseStatus.data!!)
        } else if (responseStatus.status == Result.Status.ERROR) {
            Log.d("TAG", "resultLiveData: error ${responseStatus.status}")
            emit(Result.error<T>(responseStatus.message!!))
            emitSource(source)
        }
    }


fun <T> getValueFromDbLiveData(
    databaseQuery: () -> LiveData<T>
): LiveData<Result<T>> =
    liveData(Dispatchers.IO) {
        emit(Result.loading<T>())
        val source = databaseQuery.invoke().map { Result.success(it) }
        emitSource(source)

    }

fun <T> networkResultLiveData(
    networkCall: suspend () -> Result<T>
): LiveData<Result<T>> =
    liveData(Dispatchers.IO) {
        Log.d("TAG", "networkResultLiveData: ")
        emit(Result.loading())

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Result.Status.SUCCESS) {
            Log.d("TAG", "networkResultLiveData: success")
            emit(Result.success(responseStatus.data!!))
        } else if (responseStatus.status == Result.Status.ERROR) {
            Log.d("TAG", "networkResultLiveData: error")
            emit(Result.error(responseStatus.message))
        }
    }