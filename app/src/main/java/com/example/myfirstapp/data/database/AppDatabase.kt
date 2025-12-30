package com.example.myfirstapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myfirstapp.data.dao.UserDao
import com.example.myfirstapp.data.dao.WorkoutDao
import com.example.myfirstapp.data.entities.Workout
import com.example.myfirstapp.data.entities.User

@Database(
    entities = [User::class, Workout::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}