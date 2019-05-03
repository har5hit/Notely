package com.justadeveloper96.notely.form

import android.arch.lifecycle.LiveData
import com.justadeveloper96.notely.repo.Note

/**
 * Created by harshith on 06-03-2018.
 */
interface IForm {

    interface View{
        fun changeMode(inEdit:Boolean)
        fun isNoteValid():Boolean
        fun saveNote(note: Note)
        fun setDataToView(note: Note?)
        fun closeScreen()
        fun updateNote(note: Note)
    }

    interface Actions {
        fun getNote(id: Int): LiveData<Note>
        fun saveNote(note: Note)
    }
}