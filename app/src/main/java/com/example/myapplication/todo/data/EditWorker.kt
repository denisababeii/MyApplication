package com.example.myapplication.todo.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.todo.data.local.TodoDatabase
import com.example.myapplication.todo.data.remote.ItemApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers

class EditWorker (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("EditWorker","Started")
        val itemId = inputData.getString("itemId");
        Log.d("EditWorker","ItemId: $itemId")

        val itemDao = TodoDatabase.getDatabase(applicationContext,GlobalScope).itemDao()
        Log.d("SaveWorker",itemDao.getSize().toString())

        val item = itemDao.getByIdNotLiveData(itemId)
        Log.d("EditWorker", "Returned item $item")
        if (item != null) {
            GlobalScope.launch (Dispatchers.Main) {
                ItemApi.service.update(item._id, item)
            }
            Log.d("EditWorker", "Edited item $item")
            return Result.success();
        }
        return Result.failure();
    }
}