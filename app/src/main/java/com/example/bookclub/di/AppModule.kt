package com.example.bookclub.di

import android.content.Context
import androidx.room.Room
import com.example.bookclub.data.database.AppDatabase
import com.example.bookclub.data.dao.BookClubDao
import com.example.bookclub.data.dao.BookDao
import com.example.bookclub.data.dao.UserDao
import com.example.bookclub.data.repository.BookClubRepository
import com.example.bookclub.data.repository.BookRepository
import com.example.bookclub.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBookClubDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "book_club_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideBookClubDao(database: AppDatabase): BookClubDao {
        return database.bookClubDao()
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDao: BookDao): BookRepository {
        return BookRepository(bookDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideBookClubRepository(bookClubDao: BookClubDao): BookClubRepository {
        return BookClubRepository(bookClubDao)
    }
} 