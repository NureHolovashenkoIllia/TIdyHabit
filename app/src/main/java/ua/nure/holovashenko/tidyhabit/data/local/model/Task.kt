package ua.nure.holovashenko.tidyhabit.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val category: TaskCategory,
    val dueDate: Long,
    val isCompleted: Boolean = false
)
