package space.manokhin.myweather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class, Forecast::class], version = 11, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    companion object {
        @Volatile
        private lateinit var database: RoomDB

        private const val DATABASE_NAME = "database"

        fun getInstance(context: Context): RoomDB {
            database = Room.databaseBuilder(
                context.applicationContext,
                RoomDB::class.java,
                DATABASE_NAME
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            return database

        }
    }
    abstract fun cityDao(): CityDao
    abstract fun forecastDao(): ForecastDao
}
