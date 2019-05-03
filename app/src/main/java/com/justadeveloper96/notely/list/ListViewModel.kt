package com.justadeveloper96.notely.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.justadeveloper96.notely.repo.Note
import com.justadeveloper96.notely.repo.NoteRepository

/**
 * Created by harshith on 06-03-2018.
 */
class ListViewModel(application: Application): AndroidViewModel(application), IList.Actions
{
    override fun getData(): LiveData<List<Note>> = filterer
    private val filterer=MediatorLiveData<List<Note>>()

    private val dbSource:LiveData<List<Note>>
    private val repo:NoteRepository = NoteRepository(application)

    private val appliedFilters:MutableLiveData<Pair<Boolean,Boolean>>

    init {
        dbSource=getList()
        filterer.addSource(dbSource,{
            filterer.value=it
        })
        appliedFilters=MutableLiveData()
    }

    override fun updateItem(note: Note) {
        repo.update(note)
    }

    override fun deleteItem(note: Note) {
        repo.delete(note)
    }


    override fun getList(): LiveData<List<Note>> = repo.getAllLive()

    override fun applyFilter(filterLiked: Boolean, filterStarred: Boolean) {
        appliedFilters.value=Pair(filterLiked,filterStarred)
        filterer.removeSource(dbSource)
        filterer.addSource(dbSource,{
            var res=it
            if (filterLiked)
            {
                res=res?.filter { it.liked }
            }
            if (filterStarred)
            {
                res=res?.filter { it.starred }
            }
            filterer.value=res
        })
    }

    override fun getFilters(): LiveData<Pair<Boolean, Boolean>> = appliedFilters
}