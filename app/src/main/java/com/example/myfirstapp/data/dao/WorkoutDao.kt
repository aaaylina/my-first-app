package com.example.myfirstapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myfirstapp.data.entities.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao{
    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY created_at DESC")
    fun getWorkoutsByUser(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY title ASC")
    fun getWorkoutsSortedByTitle(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY duration ASC")
    fun getWorkoutsSortedByDuration(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY " +
            "CASE difficulty " +
            "WHEN 'Beginner' THEN 1 " +
            "WHEN 'Intermediate' THEN 2 " +
            "WHEN 'Advanced' THEN 3 " +
            "ELSE 4 END ASC")
    fun getWorkoutsSortedByDifficulty(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY rating DESC")
    fun getWorkoutsSortedByRating(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): Workout?

    @Query("UPDATE workouts SET rating = :newRating, rating_count = rating_count + 1 WHERE id = :workoutId")
    suspend fun rateWorkout(workoutId: Long, newRating: Float)

    @Query("SELECT COUNT(*) FROM workouts WHERE user_id = :userId")
    suspend fun getWorkoutCount(userId: Long): Int
}