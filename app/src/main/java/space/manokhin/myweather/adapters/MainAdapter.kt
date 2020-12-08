package space.manokhin.myweather.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_update.*
import org.w3c.dom.Text
import space.manokhin.myweather.db.City
import space.manokhin.myweather.R
import space.manokhin.myweather.db.RoomDB
import space.manokhin.myweather.activities.WeatherActivity
import space.manokhin.myweather.utils.Constants.Companion.ICON_URL

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private var dataList: MutableList<City>
    private val context: Activity
    private lateinit var database: RoomDB

    constructor(context: Activity, dataList: MutableList<City>) {
        this.context = context
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_main, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: City = dataList[position]
        database = RoomDB.getInstance(context)
        holder.apply {
            textView.text = data.name
            temperature.text = "${data.feels_like}°"
            currTemp.text = "${data.temp}°"
            Glide.with(context).load(ICON_URL(data.icon!!)).into(icon)
            bindLayout(this, data.id)
            bindEditBtn(this)
            bindDeleteBtn(this)
        }
    }

    private fun bindLayout(holder: ViewHolder, id: Int) {
        holder.layout.setOnClickListener {
            val intent = Intent(context, WeatherActivity::class.java).apply {
                Log.d("city_send", "---------------- this is city name : ${holder.textView.text}")
                putExtra("city_id", id)
            }
            startActivity(context, intent, null)
        }
    }

    private fun bindEditBtn(holder: ViewHolder) {
        holder.btEdit.setOnClickListener{
            val d: City = dataList[holder.adapterPosition]
            val id: Int = d.id
            val name = d.name
            val dialog = Dialog(context)

            dialog.setContentView(R.layout.dialog_update)

            val width: Int = WindowManager.LayoutParams.MATCH_PARENT
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
            dialog.edit_text.setText(name)
            dialog.show()

            bindUpdateBtn(dialog, id)
        }
    }

    private fun bindUpdateBtn(dialog: Dialog, city_id: Int) {
        val editText: EditText = dialog.findViewById(R.id.edit_text)
        val btUpdate: Button = dialog.findViewById(R.id.bt_update)
        btUpdate.setOnClickListener {
            dialog.dismiss()
            val name = editText.text.toString().trim()
            database.cityDao().updateName(city_id, name)
            dataList.clear()
            dataList.addAll(database.cityDao().getAll())
            notifyDataSetChanged()
        }
    }

    private fun bindDeleteBtn(holder: ViewHolder) {
        holder.btDelete.setOnClickListener {
            val d = dataList[holder.adapterPosition]
            database.cityDao().delete(d)
            val position = holder.adapterPosition
            dataList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size)
        }
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var textView: TextView
        var icon: ImageView
        var temperature: TextView
        var currTemp: TextView
        var btEdit: ImageView
        var btDelete: ImageView
        var layout: LinearLayout

        constructor(itemView: View) : super(itemView) {
            textView = itemView.findViewById(R.id.text_view)
            icon = itemView.findViewById(R.id.icon)
            temperature = itemView.findViewById(R.id.temperature)
            currTemp = itemView.findViewById(R.id.currTemp)
            btEdit = itemView.findViewById(R.id.bt_edit)
            btDelete = itemView.findViewById(R.id.bt_delete)
            layout = itemView.findViewById(R.id.layout)
        }

    }

}
