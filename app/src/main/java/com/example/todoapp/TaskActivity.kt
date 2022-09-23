package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    val labels = arrayOf("Personal" ,
        "Banking" ,
        "Business" ,
        "Shopping" ,
        "College"
    )

    lateinit var myCalendar: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finaldate = 0L
    var finalTime = 0L

    val db by lazy{
           AppDatabase.getDatabase(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        dateET.setOnClickListener(this)

        timeET.setOnClickListener(this)

        btnSave.setOnClickListener(this)

        setSpinner()
    }

    private fun setSpinner() {

        val adapter = ArrayAdapter<String>(this ,
        android.R.layout.simple_spinner_dropdown_item ,
        labels)

        labels.sort()

        spinnerCategory.adapter = adapter
    }

    override fun onClick(view : View) {
           when(view.id){
                R.id.dateET ->{
                    setListener()
                }

                R.id.timeET -> {
                    setTimeListener()
                }

               R.id.btnSave ->{
                   saveToDB()
               }
           }
    }



    private fun setTimeListener() {

        myCalendar = Calendar.getInstance()
        timeSetListener = TimePickerDialog.OnTimeSetListener() { t : TimePicker, hourOfDay: Int, min: Int->
            myCalendar.set(Calendar.HOUR_OF_DAY , hourOfDay)
            myCalendar.set(Calendar.MINUTE , min)

            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            this , timeSetListener ,
            myCalendar.get(Calendar.HOUR_OF_DAY) ,
            myCalendar.get(Calendar.MINUTE) ,
            false
        )


        timePickerDialog.show()
    }

    private fun updateTime() {
        val myFormat = "h:mm a"
        val sdf = SimpleDateFormat(myFormat)

        finalTime = myCalendar.time.time
        timeET.setText(sdf.format(myCalendar.time))
    }




    private fun setListener() {
        
        myCalendar = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { d: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this , dateSetListener ,
            myCalendar.get(Calendar.YEAR) ,
            myCalendar.get(Calendar.MONTH) ,
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }



    private fun updateDate() {
        //Mon , 12 July 2022
        val myFormat = "EEE , d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)

        finaldate = myCalendar.time.time
        dateET.setText(sdf.format(myCalendar.time))

        timeInpLay.visibility = View.VISIBLE
    }



    private fun saveToDB() {

        val title = titleInpLay.editText?.text.toString()
        val task = taskInpLay.editText?.text.toString()
        val category = spinnerCategory.selectedItem.toString()

        if(title != "" && task != "" && category != "" && finaldate != 0L &&finalTime != 0L) {
            GlobalScope.launch(Dispatchers.Main) {
                val id = withContext(Dispatchers.IO) {
                    return@withContext db.todoDao().insertTask(
                        TodoModel(title, task, category, finaldate, finalTime)
                    )
                }

                finish()

            }

        }
        else{
            if(task == ""){
                Toast.makeText(this , "Please enter task" , Toast.LENGTH_SHORT).show()
            }
            else if(title == ""){
                Toast.makeText(this , "Please enter title" , Toast.LENGTH_SHORT).show()
            }
            else if(category == ""){
                Toast.makeText(this , "Please select category" , Toast.LENGTH_SHORT).show()
            }
            else if(finaldate == 0L){
                Toast.makeText(this , "Please select Date" , Toast.LENGTH_SHORT).show()
            }
            else if(finalTime == 0L){
                Toast.makeText(this , "Please select time" , Toast.LENGTH_SHORT).show()
            }
        }
    }

}