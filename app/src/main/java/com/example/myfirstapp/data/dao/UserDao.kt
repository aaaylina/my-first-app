package com.example.myfirstapp.data.dao

import androidx.room.*
import com.example.myfirstapp.data.entities.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email AND deleted_at IS NULL")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id AND deleted_at IS NULL")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmailIncludingDeleted(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getAnyUserById(id: Long): User?

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET deleted_at = :timestamp WHERE id = :userId")
    suspend fun softDeleteUser(userId: Long, timestamp: Long)

    @Query("UPDATE users SET deleted_at = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: Long)

    @Query("DELETE FROM users WHERE deleted_at IS NOT NULL AND deleted_at < :threshold")
    suspend fun permanentlyDeleteOldUsers(threshold: Long)

    @Query("UPDATE users SET last_login = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, timestamp: Long)
}