package space.manokhin.myweather.db

import androidx.room.*

@Entity(tableName = "forecast", indices = [(Index(value = ["city_id"]))], foreignKeys = [
    ForeignKey(entity = City::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("city_id"),
        onDelete = ForeignKey.CASCADE
    )
])
data class Forecast(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "city_id") var city_id: Int = 0,
    @ColumnInfo(name = "weatherIcon") var weatherIcon: String = "",
    @ColumnInfo(name = "currentDay") var currentDay: Int = 0,
    @ColumnInfo(name = "weather") var weather: String = "",
    @ColumnInfo(name = "tempDay") var tempDay: Int = 0,
    @ColumnInfo(name = "tempNight") var tempNight: Int = 0
)