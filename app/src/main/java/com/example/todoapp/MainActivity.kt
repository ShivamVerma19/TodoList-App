package com.example.todoapp


import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*




class MainActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    val todoAdapter = TodoAdapter(list)
    val db by lazy{
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        btnAddTask.setOnClickListener{
            startActivity(Intent(this , TaskActivity::class.java))
        }


       todoRv.apply{
           layoutManager = LinearLayoutManager(this@MainActivity)
           adapter = this@MainActivity.todoAdapter
       }



        db.todoDao().getTask().observe(this , androidx.lifecycle.Observer {
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                todoAdapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                todoAdapter.notifyDataSetChanged()
            }
        })

        initSwipe()
    }


    fun initSwipe() {
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or
                    ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(todoAdapter.getItemId(position))
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().updateTask(todoAdapter.getItemId(position))
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon : Bitmap


                    if(dX > 0){

                        paint.color = Color.parseColor("#388E3C")

                        icon = BitmapFactory.decodeResource(resources , R.mipmap.ic_check_white_png)

                        //color
                        canvas.drawRect(
                            itemView.left.toFloat() ,
                            itemView.top.toFloat() ,
                            itemView.left.toFloat() + dX ,
                            itemView.bottom.toFloat() ,
                            paint
                        )


                        //icon
                        canvas.drawBitmap(
                            icon ,
                            itemView.left.toFloat()  ,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/ 2,
                            paint
                        )
                    }
                    else{

                        paint.color = Color.parseColor("#D32F2F")

                        icon = BitmapFactory.decodeResource(resources , R.mipmap.ic_delete_white_png)

                        canvas.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat() ,
                            itemView.right.toFloat() ,
                            itemView.bottom.toFloat() ,
                            paint
                        )


                        canvas.drawBitmap(
                            icon ,
                            itemView.right.toFloat()  - icon.width.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/ 2,
                            paint
                        )
                    }

                    viewHolder.itemView.translationX = dX
                }

                else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(todoRv)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu , menu)

        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(mItem: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(mItem: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }



    fun displayTodo(newText: String = "") {
        db.todoDao().getTask().observe(this , androidx.lifecycle.Observer {
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText,true)
                    }
                )

                todoAdapter.notifyDataSetChanged()
            }
        })
    }


}

