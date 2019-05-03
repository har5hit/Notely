package com.justadeveloper96.notely.repo

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * Created by harshith on 06-03-2018.
 */
@Dao
abstract class NoteDao{
    @get:Query("SELECT * FROM Note ORDER BY createdAt DESC")
    abstract val all: LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE id LIKE :search")
    abstract fun find(search: Int): LiveData<Note>

    @Insert
    abstract fun insertAll(note: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(note: Note)

    @Update
    abstract fun update(note: Note)

    @Delete
    abstract fun delete(note: Note)

    @Query("DELETE FROM Note")
    abstract fun destroy()
}