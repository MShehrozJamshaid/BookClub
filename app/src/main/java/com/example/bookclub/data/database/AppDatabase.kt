package com.example.bookclub.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bookclub.data.converter.BookClubTypeConverters
import com.example.bookclub.data.dao.BookClubDao
import com.example.bookclub.data.dao.BookDao
import com.example.bookclub.data.dao.UserDao
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.data.model.User

@Database(
    entities = [Book::class, User::class, BookClub::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BookClubTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun bookClubDao(): BookClubDao
}