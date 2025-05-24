package ua.nure.holovashenko.tidyhabit.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val level: Int = 1,
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActiveDate: String = ""
)
