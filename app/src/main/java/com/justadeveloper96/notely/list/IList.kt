package com.justadeveloper96.notely.list

import android.arch.lifecycle.LiveData
import com.justadeveloper96.notely.repo.Note

/**
 * Created by harshith on 06-03-2018.
 */
interface IList {

    interface View{
        fun addNewNote()
        fun updateList(notes : List<Note>)
        fun openDrawer()
        fun closeDrawer()
        fun prefillFilters()
        fun openNote(note: Note)
        fun updateNote(note: Note)
        fun deleteNote(note: Note)
        fun isFilterApplied():Boolean
        fun applyFilters()
        fun showEmptyNoteScreen()
        fun showEmptyResultScreen()
        fun showDeleteConfirmPopup(note: Note)
    }
    interface Actions{
        fun getList():LiveData<List<Note>>;
        fun applyFilter(filterLiked: Boolean=false, filterStarred:Boolean=false);
        fun updateItem(note:Note)
        fun deleteItem(note:Note)
        fun getData():LiveData<List<Note>>
        fun getFilters():LiveData<Pair<Boolean,Boolean>>
    }
}