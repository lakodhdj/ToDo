package com.example.todo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

data class Task(val title: String, val deadline: Long)

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var userData: EditText
    private lateinit var button: Button
    private lateinit var dateButton: Button
    private lateinit var adapter: ArrayAdapter<Task>
    private val todos: MutableList<Task> = mutableListOf()
    private var selectedDateMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        userData = findViewById(R.id.user_data)
        button = findViewById(R.id.button)
        dateButton = findViewById(R.id.btnPickDate)

        adapter = object : ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, todos) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val task = getItem(position)
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                (view as TextView).text = "${task?.title} (до ${sdf.format(Date(task?.deadline ?: 0))})"
                return view
            }
        }

        listView.adapter = adapter

        button.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text.isNotEmpty() && selectedDateMillis > 0L) {
                val task = Task(text, selectedDateMillis)
                todos.add(task)
                todos.sortBy { it.deadline }
                adapter.notifyDataSetChanged()
                userData.text.clear()
                selectedDateMillis = 0L
                dateButton.text = "Выбрать дату"
            } else {
                Toast.makeText(this, "Введите задачу и выберите дату", Toast.LENGTH_SHORT).show()
            }
        }

        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(y, m, d, 0, 0, 0)
                selectedDateMillis = selectedCalendar.timeInMillis
                dateButton.text = "Дата: ${d}.${m + 1}.$y"
            }, year, month, day)

            datePicker.show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val task = adapter.getItem(position)
            Toast.makeText(this, "Нажмите и удерживайте, чтобы удалить: ${task?.title}", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val task = adapter.getItem(position)
            AlertDialog.Builder(this)
                .setTitle("Удалить задачу?")
                .setMessage("Вы уверены, что хотите удалить \"${task?.title}\"?")
                .setPositiveButton("Да") { _, _ ->
                    adapter.remove(task)
                }
                .setNegativeButton("Нет", null)
                .show()
            true
        }
    }
}
