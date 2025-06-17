package ua.nure.holovashenko.tidyhabit.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.dao.UserDao
import ua.nure.holovashenko.tidyhabit.data.local.db.AppDatabase
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tasks_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, userPrefs: UserPreferences): UserRepository = UserRepository(userDao, userPrefs)

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao, userPrefs: UserPreferences): TaskRepository = TaskRepository(taskDao, userPrefs)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferences(userPreferences: SharedPreferences): UserPreferences {
        return UserPreferences(userPreferences)
    }
}
