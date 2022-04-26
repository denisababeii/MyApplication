package com.example.myapplication.todo.data

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.myapplication.core.TAG
import com.example.myapplication.core.Result
import com.example.myapplication.todo.data.local.ItemDao
import com.example.myapplication.todo.data.remote.ItemApi

class ItemRepository(private val itemDao: ItemDao) {

    val items = itemDao.getAll()

    suspend fun refresh(): Result<Boolean> {
        try {
            val items = ItemApi.service.find()
            for (item in items) {
                itemDao.insert(item)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(itemId: String): LiveData<Item> {
        return itemDao.getById(itemId)
    }

    @SuppressLint("RestrictedApi")
    private fun startSaveJob(itemId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = Data.Builder()
            .put("itemId",itemId)
            .build()
        val myWork = OneTimeWorkRequest.Builder(SaveWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance().enqueue(myWork);
    }

    @SuppressLint("RestrictedApi")
    private fun startEditJob(itemId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = Data.Builder()
            .put("itemId",itemId)
            .build()
        val myWork = OneTimeWorkRequest.Builder(EditWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance().enqueue(myWork);
    }

    suspend fun save(item: Item): Result<Item> {
        try {
            val createdItem = ItemApi.service.create(item)
            itemDao.insert(createdItem)
            return Result.Success(createdItem)
        } catch(e: Exception) {
            Log.d("save","failed to save on server")
            Log.d("save", "itemDao size: "+itemDao.getSize().toString())
            itemDao.insert(item)
            Log.d("save", "itemDao size: "+itemDao.getSize().toString())
            Log.d("save","saved locally ${item._id}")
            startSaveJob(item._id)
            Log.d("save","enqueued job")
            return Result.Error(e)
        }
    }

    suspend fun update(item: Item): Result<Item> {
        try {
            val updatedItem = ItemApi.service.update(item._id, item)
            itemDao.update(updatedItem)
            return Result.Success(updatedItem)
        } catch(e: Exception) {
            Log.d("edit","failed to edit on server")
            itemDao.update(item)
            Log.d("edit","edited locally id ${item._id}")
            startEditJob(item._id)
            Log.d("edit","enqueued job")
            return Result.Error(e)
        }
    }
}