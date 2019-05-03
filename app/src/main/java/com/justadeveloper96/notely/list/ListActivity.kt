package com.justadeveloper96.notely.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.justadeveloper96.notely.R
import com.justadeveloper96.notely.form.FormActivity
import com.justadeveloper96.notely.repo.Note
import kotlinx.android.synthetic.main.content_notes_list.*
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.widget.CheckedTextView
import com.justadeveloper96.notely.helpers.Constants
import com.justadeveloper96.notely.helpers.Font
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.filter_options.*
/**
 * Created by harshith on 06-03-2018.
 */

class ListActivity : AppCompatActivity(), View.OnClickListener,IList.View {


    private var filterLikeApplied =false
    private var filterStarApplied =false

    private val keplarFont by lazy {  Font.getFont(baseContext,Constants.keplarStdFont) }
    private val adapter by lazy { ListAdapter() }
    private val viewmodel by lazy { ViewModelProviders.of(this).get(ListViewModel::class.java) as IList.Actions }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()

        //get filters from viewmodel if there was any before configuration change
        viewmodel.getFilters().observe(this,Observer<Pair<Boolean,Boolean>>{t->
            t?.let {
                filterLikeApplied=it.first
                filterStarApplied=it.second
            }
        })

        val mDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, list_toolbar, R.string.acc_drawer_open, R.string.acc_drawer_close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)

                //slide the main view with respect to drawer opening and closing
                val moveFactor = nav_view.width * slideOffset
                notes_list_root.translationX = -moveFactor

            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //discard filters that are not applied
                prefillFilters()
            }
        }

        drawer_layout.addDrawerListener(mDrawerToggle)

        viewmodel.getData().observe(this, Observer<List<Note>> { t ->
            t?.let {
                //remove all empty texts
                tv_empty_filter_list.visibility = View.GONE
                tv_empty_list.visibility = View.GONE

                //update the sortedlist in adapter
                updateList(t)

                //if list is empty check if there are no notes
                if(t.isEmpty()) {

                    //if its empty because of results
                    if(isFilterApplied()) {
                        showEmptyResultScreen()
                    }else{
                        showEmptyNoteScreen()
                    }
                }
            }

        })
    }

    private fun initViews() {

        //set toolbar
        setSupportActionBar(list_toolbar)

        //custom font apply
        toolbar_title.typeface = keplarFont
        tv_empty_list.typeface=keplarFont
        tv_empty_filter_list.typeface=keplarFont

        //recyclerview setup
        recyclerView.layoutManager=LinearLayoutManager(baseContext)
        recyclerView.adapter=adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerView.itemAnimator = SlideInLeftAnimator()

        //note item actions handling
        adapter.actionlistener.subscribe {
            when(it.second){
                ListAdapter.UPDATE->{
                    updateNote(it.first)
                }
                ListAdapter.OPEN->{
                    openNote(it.first)
                }
                ListAdapter.DELETE->{
                    showDeleteConfirmPopup(it.first)
                }}
        }

        //listener binding
        filter_apply.setOnClickListener(this)
        filter_clear.setOnClickListener(this)
        filter_iv_close.setOnClickListener(this)
        filter_cb_like.setOnClickListener(this)
        filter_cb_starred.setOnClickListener(this)
        tv_empty_list.setOnClickListener(this)
    }

    override fun updateList(notes: List<Note>)  {
        adapter.update(notes)
    }

    override fun showDeleteConfirmPopup(note: Note) {
        AlertDialog.Builder(this).setTitle(getString(R.string.popup_note_delete_title)).setMessage(getString(R.string.popup_note_delete_description))
                .setPositiveButton(getString(R.string.popup_note_action_delete),{ _, _->
                    deleteNote(note)
                })
                .setNegativeButton(getString(R.string.popup_note_delete_dismiss),null)
                .show()
    }

    override fun updateNote(note: Note) {
        viewmodel.updateItem(note)
    }

    override fun deleteNote(note: Note) {
        viewmodel.deleteItem(note)
    }

    override fun openNote(note: Note) {
        startActivity(Intent(baseContext,FormActivity::class.java).putExtra(Constants.KEY_ID,note.id))
    }

    override fun showEmptyNoteScreen() {
        tv_empty_list.visibility = View.VISIBLE
    }

    override fun showEmptyResultScreen() {
        tv_empty_filter_list.visibility = View.VISIBLE
    }


    override fun addNewNote() {
        startActivity(Intent(this,FormActivity::class.java))
    }

    override fun applyFilters() {
        viewmodel.applyFilter(filter_cb_like.isChecked, filter_cb_starred.isChecked)
        closeDrawer()
        invalidateOptionsMenu()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //different menu resource for filter icon enable disable based on filter applied or not
        if(isFilterApplied())
        {
            menuInflater.inflate(R.menu.menu_list_active,menu)
        }else {
            menuInflater.inflate(R.menu.menu_list, menu)
        }
        return true
    }

    override fun isFilterApplied():Boolean= filterLikeApplied || filterStarApplied

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_add->{
                addNewNote()
            }
            R.id.action_filter->{
                openDrawer()
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.filter_cb_like,R.id.filter_cb_starred->{
                (v as CheckedTextView).isChecked=!v.isChecked
            }
            R.id.filter_apply->{
                applyFilters()
            }
            R.id.filter_iv_close->{
                closeDrawer()
            }
            R.id.tv_empty_list->{
                addNewNote()
            }
            R.id.filter_clear->{
                //remove filter checks and apply them
                filter_cb_like.isChecked=false
                filter_cb_starred.isChecked=false
                applyFilters()
            }
        }
    }

    override fun openDrawer() {
        drawer_layout.openDrawer(Gravity.END)
    }

    override fun closeDrawer() {
        drawer_layout.closeDrawer(Gravity.END)
    }

    override fun prefillFilters() {
        filter_cb_like.isChecked= filterLikeApplied
        filter_cb_starred.isChecked= filterStarApplied
    }

}
