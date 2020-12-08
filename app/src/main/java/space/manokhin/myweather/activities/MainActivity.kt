package space.manokhin.myweather.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_weather.*
import space.manokhin.myweather.db.City
import space.manokhin.myweather.R
import space.manokhin.myweather.db.RoomDB
import space.manokhin.myweather.adapters.MainAdapter

class MainActivity : AppCompatActivity() {
    lateinit var background_main: LinearLayout
    lateinit var editText: EditText
    lateinit var btAdd: Button
    lateinit var btReset: Button
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var database: RoomDB
    lateinit var mainAdapter: MainAdapter

    lateinit var dataList: MutableList<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initiateViews()

        val value = intent.getIntExtra("background", R.drawable.brightest_sky_gradient)
        background_main.setBackgroundResource(value)
        database = RoomDB.getInstance(this@MainActivity)
        dataList = database.cityDao().getAll()

        linearLayoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = linearLayoutManager
        mainAdapter = MainAdapter(this@MainActivity, dataList)

        recyclerView.adapter = mainAdapter

        btAdd.setOnClickListener {
            val name = editText.text.toString().trim()
            if (name != "") {
                val city = City()
                city.name = name
                database.cityDao().insertAll(city)
                editText.setText("")
                dataList.clear()
                dataList.addAll(database.cityDao().getAll())
                val intent = Intent(this@MainActivity, WeatherActivity::class.java).apply {
                    Log.d("city_send", "---------------- this is city name : $name")
                    putExtra("city_id", database.cityDao().findByName(name).id)
                }
                startActivity(intent)
                //mainAdapter.notifyDataSetChanged()

            }
        }

        btReset.setOnClickListener {
            database.cityDao().reset(dataList)
            dataList.clear()
            dataList.addAll(database.cityDao().getAll())
            mainAdapter.notifyDataSetChanged()
        }
    }

    private fun initiateViews() {
        background_main = findViewById(R.id.background_main)
        editText = findViewById(R.id.edit_text)
        btAdd = findViewById(R.id.bt_add)
        btReset = findViewById(R.id.bt_reset)
        recyclerView = findViewById(R.id.recycler_view)
    }
}
