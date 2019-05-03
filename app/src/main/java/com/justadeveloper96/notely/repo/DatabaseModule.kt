package com.bigrattle.architecturecomponents.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.justadeveloper96.notely.repo.Note
import com.justadeveloper96.notely.repo.NoteDao


/**
 * Created by harshith on 06-03-2018.
 */
@Database(entities = [(Note::class)], version = 5)
abstract class DatabaseModule : RoomDatabase() {
    abstract fun notesDao(): NoteDao
}