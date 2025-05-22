package ua.nure.holovashenko.tidyhabit.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.dao.UserDao
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.User

@Database(entities = [Task::class, User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
}