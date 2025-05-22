package ua.nure.holovashenko.tidyhabit.data.local.db

import androidx.room.TypeConverter
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory

class Converters {
    @TypeConverter
    fun fromCategory(value: TaskCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): TaskCategory = TaskCategory.valueOf(value)
}
