package com.justadeveloper96.notely.repo

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.bigrattle.architecturecomponents.db.DatabaseModule
import com.justadeveloper96.notely.helpers.Utils

/**
 * Created by harshith on 06-03-2018.
 */
class RoomModule(){
    companion object {
        private var INSTANCE: DatabaseModule? = null
        fun getDatabase(context: Context): DatabaseModule {
            if (INSTANCE==null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseModule::class.java, "my_notes_db")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return INSTANCE!!
        }
    }
}