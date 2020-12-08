package space.manokhin.myweather.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") var name: String? = "",
    @ColumnInfo(name = "temperature") var temp: Int? = 0,
    @ColumnInfo(name = "feels_like") var feels_like: Double? = 0.0,
    @ColumnInfo(name = "wind") var wind: Double? = 0.0,
    @ColumnInfo(name = "weather") var weather: String? = "",
    @ColumnInfo(name = "sunrise") var sunrise: Int? = 0,
    @ColumnInfo(name = "sunset") var sunset: Int? = 0,
    @ColumnInfo(name = "icon") var icon: String? = "",
    @ColumnInfo(name = "lon") var lon: String? = "",
    @ColumnInfo(name = "lat") var lat: String? = "",
    @ColumnInfo(name = "dt") var dt: Int? = 0
)