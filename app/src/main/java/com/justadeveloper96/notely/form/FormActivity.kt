package com.justadeveloper96.notely.form

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.justadeveloper96.notely.R
import com.justadeveloper96.notely.helpers.Font
import com.justadeveloper96.notely.helpers.Utils
import com.justadeveloper96.notely.repo.Note
import kotlinx.android.synthetic.main.activity_form.*
import android.support.design.widget.AppBarLayout
import android.util.TypedValue
import com.justadeveloper96.notely.helpers.Constants

/**
 * Created by harshith on 06-03-2018.
 */



class FormActivity : AppCompatActivity(),IForm.View {

    private var id=0
    private val viewmodel by lazy { ViewModelProviders.of(this).get(FormViewModel::class.java) as IForm.Actions}
    private var item:Note?=null
    private var inEdit:Boolean=true
    private val keplarFont by lazy {  Font.getFont(baseContext,Constants.keplarStdFont) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        initViews()

        //get id if opening a saved note
        id=intent.getIntExtra(Constants.KEY_ID,0)

        if (id==0)
        {
            item=Note()
            changeMode(true)
        }else{
            changeMode(false)
            viewmodel.getNote(id).observe(this, Observer<Note> { t ->
                if(t!=null) {
                    fillData(t)
                }
            })
        }
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsing.setCollapsedTitleTypeface(keplarFont)
        collapsing.setExpandedTitleTypeface(keplarFont)
        form_title.typeface=keplarFont
        ed_title.typeface=keplarFont
    }

    private fun fillData(t: Note) {
        item=t
        changeTitle(t.title,Utils.formatToRelativeDateString(t.updatedAt))
        setDataToView(t)
    }

    /***
     * change from view mode to edit mode
     */
    override fun changeMode(inEdit:Boolean){
        this.inEdit=inEdit
        appbar.setExpanded(!this.inEdit,true)
        ed_note.isEnabled=this.inEdit
        if (inEdit){
            val params=appbar.layoutParams
            val tv = TypedValue()

            //get action bar default size and set it to appbar
            if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                params.height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                appbar.layoutParams=params
            }
            disableScroll()
            ed_title.visibility=View.VISIBLE
            ed_title.requestFocus()
        } else {
            enableScroll()
            ed_title.visibility=View.GONE
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //options menu change depending upon if its read mode or edit mode
        if (inEdit)
        {
            menuInflater.inflate(R.menu.menu_form_add,menu)
        }else
        {
            menuInflater.inflate(R.menu.menu_form_edit,menu)
        }
        return true
    }

    override fun saveNote(note: Note) {
        viewmodel.saveNote(note)
        closeScreen()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_undo->{
                setDataToView(this.item)
            }
            R.id.action_save->{
                //check if title is set to avoid blank note
                if(!isNoteValid()){
                    return true
                }

                //if it was a new note then create new and save else, edit the old note to update
                if(id==0)
                {
                    val note=Note(title = ed_title.text.toString().trim(),note=ed_note.text.toString().trim())
                    saveNote(note)
                }else{
                    this.item!!.title=ed_title.text.toString().trim()
                    this.item!!.note=ed_note.text.toString().trim()
                    updateNote(this.item!!)
                }
            }
            R.id.action_edit->{
                changeMode(true)
                changeTitle()
            }

            android.R.id.home->{
                closeScreen()
            }
        }
        return true
    }

    override fun updateNote(note: Note) {
        note.updatedAt =System.currentTimeMillis()
        saveNote(note)
    }

    override fun isNoteValid(): Boolean {
        return if (ed_title.text.trim().toString().isEmpty())
        {
            ed_title.error = getString(R.string.error_form_title_empty)
            false
        }else true
    }

    private fun changeTitle(title:String="",subtitle:String=""){
        form_title.text=title
        form_last_seen.text=if(subtitle.isEmpty()) "" else "Last Updated: "+subtitle
    }

    private fun enableScroll() {
        val params = collapsing.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        collapsing.layoutParams = params
    }

    private fun disableScroll() {
        val params = collapsing.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        collapsing.layoutParams = params
    }

    override fun setDataToView(note: Note?) {
        ed_title.setText(note?.title)
        ed_note.setText(note?.note)
    }

    override fun closeScreen() {
        finish()
    }

}
