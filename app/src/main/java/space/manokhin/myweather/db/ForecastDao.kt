package space.manokhin.myweather.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ForecastDao {
    @Insert(onConflict = REPLACE)
    fun insertAll(forecasts: List<Forecast>)

    @Query("SELECT * FROM forecast WHERE city_id = :city_id ORDER BY currentDay")
    fun getByCityId(city_id: Int): MutableList<Forecast>

    @Delete
    fun reset(forecasts: List<Forecast>)

    @Query("SELECT * FROM forecast")
    fun getAll(): List<Forecast>
}