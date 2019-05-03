package com.justadeveloper96.notely.repo

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by harshith on 06-03-2018.
 */
class NoteRepository(context: Context) {
    val dao:NoteDao
    init {
        dao=RoomModule.getDatabase(context).notesDao()
    }

    fun get(id:Int)=dao.find(id)

    fun insert(note:Note)
    {
        run {  dao.insert(note) }
    }

    fun update(note: Note)
    {
        run { dao.update(note) }
    }

    fun delete(note:Note)
    {
        run {  dao.delete(note)}
    }

    fun run(function:()->Unit)
    {
        Thread(function).start()
    }

    fun getAllLive(): LiveData<List<Note>> {
        return dao.all
    }
}