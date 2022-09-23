package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(user : TodoModel):Long


    @Query("SELECT * FROM TodoModel WHERE isFinished != 1")
    fun getTask() : LiveData<List<TodoModel>>


    @Query("UPDATE TodoModel set isFinished = 1 WHERE id = :uid")
    fun updateTask(uid : Long)



    @Query("DELETE FROM TodoModel WHERE id = :uid")
    fun deleteTask(uid : Long)
}