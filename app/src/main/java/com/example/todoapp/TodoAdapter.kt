package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt


class TodoAdapter(val list : List<TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_todo ,
        parent , false)

        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class TodoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(todoModel: TodoModel) {
            with(itemView){
                val colors = resources.getIntArray(R.array.random_color)
                val color = colors[Random().nextInt(colors.size)]
                viewColorTag.setBackgroundColor(color)

                titleCardView.text = todoModel.title
                taskSubCardView.text = todoModel.description
                catCardView.text = todoModel.category

                updateTime(todoModel.time)
                updateDate(todoModel.date)
            }
        }

        private fun updateDate(date: Long) {
            val myFormat = "EEE , d MMM yyyy"
            val sdf = SimpleDateFormat(myFormat)
            itemView.dateShow.setText(sdf.format(Date(date)))
        }

        private fun updateTime(time: Long) {
            val myFormat = "h:mm a"
            val sdf = SimpleDateFormat(myFormat)
            itemView.timeShow.setText(sdf.format(Date(time)))
        }


    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }
}