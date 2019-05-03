package com.justadeveloper96.notely.form

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.justadeveloper96.notely.repo.Note
import com.justadeveloper96.notely.repo.NoteRepository

/**
 * Created by harshith on 06-03-2018.
 */
class FormViewModel(application: Application):AndroidViewModel(application), IForm.Actions
{
    override fun getNote(id: Int) = repo.get(id)

    private val repo:NoteRepository = NoteRepository(application)

    override fun saveNote(note: Note) {
        repo.insert(note)
    }
}