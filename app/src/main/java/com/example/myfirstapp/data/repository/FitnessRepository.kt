package com.example.myfirstapp.data.repository

import com.example.myfirstapp.App
import com.example.myfirstapp.data.dao.UserDao
import com.example.myfirstapp.data.dao.WorkoutDao
import com.example.myfirstapp.data.entities.User
import com.example.myfirstapp.data.entities.Workout
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import com.example.myfirstapp.R

class FitnessRepository(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao
) {

    suspend fun registerUser(email: String, password: String, username: String): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception(App.instance.getString(R.string.exist_error)))
            } else {
                val user = User(
                    email = email,
                    password = password,
                    username = username
                )
                val userId = userDao.insertUser(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmailIncludingDeleted(email)
            if (user == null) {
                Result.failure(Exception(App.instance.getString(R.string.not_found)))
            } else if (user.password != password) {
                Result.failure(Exception(App.instance.getString(R.string.invalid_password)))
            } else if (user.deletedAt != null) {
                val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
                if (user.deletedAt > sevenDaysAgo) {
                    Result.success(user)
                } else {
                    userDao.permanentlyDeleteOldUsers(System.currentTimeMillis())
                    Result.failure(Exception(App.instance.getString(R.string.perm_deleted)))
                }
            } else {
                userDao.updateLastLogin(user.id, System.currentTimeMillis())
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun softDeleteUser(userId: Long) {
        userDao.softDeleteUser(userId, System.currentTimeMillis())
    }

    suspend fun restoreUser(userId: Long) {
        userDao.restoreUser(userId)
    }

    suspend fun permanentlyDeleteUser(userId: Long) {
        val user = userDao.getUserById(userId)
        user?.let {
            userDao.softDeleteUser(userId, System.currentTimeMillis())
            userDao.permanentlyDeleteOldUsers(System.currentTimeMillis())
        }
    }

    suspend fun updateUserProfile(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getAnyUserById(userId)
    }


    suspend fun addWorkout(workout: Workout): Result<Long> {
        return try {
            val workoutId = workoutDao.insertWorkout(workout)
            Result.success(workoutId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getWorkouts(userId: Long, sortType: SortType): Flow<List<Workout>> {
        return when (sortType) {
            SortType.DATE -> workoutDao.getWorkoutsByUser(userId)
            SortType.TITLE -> workoutDao.getWorkoutsSortedByTitle(userId)
            SortType.DURATION -> workoutDao.getWorkoutsSortedByDuration(userId)
            SortType.DIFFICULTY -> workoutDao.getWorkoutsSortedByDifficulty(userId)
            SortType.RATING -> workoutDao.getWorkoutsSortedByRating(userId)
        }
    }

    suspend fun rateWorkout(workoutId: Long, rating: Float) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            val newRating = ((it.rating * it.ratingCount) + rating) / (it.ratingCount + 1)
            workoutDao.rateWorkout(workoutId, newRating)
        }
    }

    suspend fun cleanupDeletedUsers() {
        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        userDao.permanentlyDeleteOldUsers(sevenDaysAgo)
    }

    suspend fun getWorkoutCount(userId: Long): Int {
        return workoutDao.getWorkoutCount(userId)
    }
}

enum class SortType {
    DATE, TITLE, DURATION, DIFFICULTY, RATING
}