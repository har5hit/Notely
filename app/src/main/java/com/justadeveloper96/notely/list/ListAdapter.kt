package com.justadeveloper96.notely.list

import android.databinding.DataBindingUtil
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.daimajia.swipe.SwipeLayout
import com.justadeveloper96.notely.R
import com.justadeveloper96.notely.databinding.ListItemNoteBinding
import com.justadeveloper96.notely.repo.Note
import io.reactivex.subjects.PublishSubject

/**
 * Created by harshith on 06-03-2018.
 */
class ListAdapter: RecyclerView.Adapter<ListAdapter.NoteViewHolder>() {

    companion object {
         val DELETE=293
         val UPDATE=376
         val OPEN=187
    }


    val actionlistener= PublishSubject.create<Pair<Note,Int>>()!!

    val list:SortedList<Note>
    init {
        list=SortedList(Note::class.java,object : SortedListAdapterCallback<Note>(this){
            override fun areItemsTheSame(item1: Note?, item2: Note?): Boolean = item1!!.id == item2!!.id

            override fun compare(o1: Note?, o2: Note?): Int = o2!!.createdAt.compareTo(o1!!.createdAt)

            override fun areContentsTheSame(oldItem: Note?, newItem: Note?): Boolean = oldItem!!.updatedAt == newItem!!.updatedAt

        })
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layout=DataBindingUtil.inflate<ListItemNoteBinding>(LayoutInflater.from(parent.context),R.layout.list_item_note,parent,false)
        return NoteViewHolder(layout)
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.setNote(list.get(position))
    }

    fun update(data:List<Note>)
    {
        list.replaceAll(data)
    }

    inner class NoteViewHolder constructor(val binding: ListItemNoteBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            val item=list.get(adapterPosition)
            when(v?.id){
                R.id.cb_like->{
                    item.liked=(v as CheckBox).isChecked
                    actionlistener.onNext(Pair(item,UPDATE))
                }
                R.id.cb_star->{
                    item.starred=(v as CheckBox).isChecked
                    actionlistener.onNext(Pair(item,UPDATE))
                }
                R.id.ll_root->{
                    actionlistener.onNext(Pair(item,OPEN))
                }
                R.id.delete->{
                    actionlistener.onNext(Pair(item,DELETE))
                }
            }
        }

        init {
            binding.cbStar.setOnClickListener(this)
            binding.cbLike.setOnClickListener(this)
            binding.delete.setOnClickListener(this)
            binding.llRoot.setOnClickListener(this)
            binding.swipeLayout.showMode= SwipeLayout.ShowMode.PullOut
            binding.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, binding.delete)
        }
    }

    override fun getItemCount(): Int = list.size()

}