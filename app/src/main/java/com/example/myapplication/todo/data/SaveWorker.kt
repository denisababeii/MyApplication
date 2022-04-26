package com.example.myapplication.todo.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.todo.data.local.TodoDatabase
import com.example.myapplication.todo.data.remote.ItemApi
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.myapplication.todo.data.local.ItemDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers

class SaveWorker (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("SaveWorker","Started")
        var itemId = inputData.getString("itemId");
        Log.d("SaveWorker", "Saved id $itemId")

        val itemDao = TodoDatabase.getDatabase(applicationContext,GlobalScope).itemDao()
        Log.d("SaveWorker",itemDao.getSize().toString())
        val items = itemDao.getAllNotLiveData()
        if (items != null) {
            for(item in items)
            {
                Log.d("SaveWorker",item._id)
            }
        }

        val item = itemDao.getByIdNotLiveData(itemId)
        itemDao.deleteById(itemId)
        if (item != null) {
            item._id=(itemDao.getSize()+1).toString()
        }
        Log.d("SaveWorker",itemDao.getSize().toString())

        Log.d("SaveWorker", "Returned item $item")
        if (item != null) {
            GlobalScope.launch (Dispatchers.Main) {
                val createdItem = ItemApi.service.create(item)
                itemDao.insert(createdItem)
            }
            Log.d("SaveWorker", "Saved item $item")
            return Result.success();
        }
        return Result.failure();
    }
}