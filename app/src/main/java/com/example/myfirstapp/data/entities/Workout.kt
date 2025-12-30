package com.example.myfirstapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id", index = true)
    val userId: Long,

    val title: String,
    val description: String,
    val category: String,
    val duration: Int,
    val difficulty: String,
    val calories: Int,
    val equipment: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "rating")
    val rating: Float = 0f,

    @ColumnInfo(name = "rating_count")
    val ratingCount: Int = 0,

    val imageUrl: String? = null,
    val isFavorite: Boolean = false
) {
    fun getEquipmentList(): List<String> {
        return if (equipment.isBlank()) {
            emptyList()
        } else {
            try {
                Gson().fromJson(equipment, object : TypeToken<List<String>>() {}.type)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}