package com.example.myapplication.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey @ColumnInfo(name = "_id") var _id: String,
//    @ColumnInfo(name = "text") var text: String,
//    @ColumnInfo(name = "restaurant") var restaurant: String,
//    @ColumnInfo(name = "stars") var stars: Int,
//    @ColumnInfo(name = "userId") var userId: String
) {
    override fun toString(): String = ""
}
