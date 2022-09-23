package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TodoModel(

    val title : String ,
    val description : String ,
    val category : String ,
    val date : Long ,
    val time : Long ,
    val isFinished : Int = 0 ,
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L
)

