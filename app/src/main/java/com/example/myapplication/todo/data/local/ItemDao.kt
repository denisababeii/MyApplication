package com.example.myapplication.todo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.todo.data.Item

@Dao
interface ItemDao {
    @Query("SELECT * from items")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE _id=:id ")
    fun getById(id: String?): LiveData<Item>

    @Query("SELECT * FROM items WHERE _id=:id ")
    fun getByIdNotLiveData(id: String?): Item

    @Query("SELECT * from items")
    fun getAllNotLiveData(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAll()

    @Query("SELECT Count(*) FROM items")
    fun getSize(): Int

    @Query("DELETE FROM items WHERE _id=:id")
    fun deleteById(id:String?)
}