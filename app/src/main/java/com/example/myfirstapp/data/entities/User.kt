package com.example.myfirstapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "email")
    val email: String,

    val password: String,
    val username: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,

    @ColumnInfo(name = "last_login")
    val lastLogin: Long? = null,

    val profileImage: String? = null,
    val fitnessLevel: String = "Beginner",
    val weight: Double? = null,
    val height: Double? = null,
    val age: Int? = null,
    val bio: String = ""
)