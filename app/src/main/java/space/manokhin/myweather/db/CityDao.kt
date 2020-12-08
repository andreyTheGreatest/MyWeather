package space.manokhin.myweather.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAll(): MutableList<City>

    @Query("SELECT * FROM city WHERE id IN (:cityIds)")
    fun loadAllByIds(cityIds: IntArray): List<City>

    @Query("SELECT * FROM city WHERE id = :cityId")
    fun findById(cityId: Int): City

    @Query("SELECT * FROM city WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): City

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg cities: City)

    @Query("UPDATE city SET temperature = :updated_temp WHERE id = :id")
    fun updateTemperature(id: Int, updated_temp: Double)

    @Query("UPDATE city SET name = :updated_name WHERE id = :id")
    fun updateName(id: Int, updated_name: String)

    @Delete
    fun reset(cities: List<City>)

    @Update
    fun update(city: City)

    @Delete
    fun delete(city: City)
}
