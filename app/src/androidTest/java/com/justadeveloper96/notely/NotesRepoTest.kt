package com.justadeveloper96.notely

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.bigrattle.architecturecomponents.db.DatabaseModule
import com.justadeveloper96.notely.repo.Note
import com.justadeveloper96.notely.repo.NoteDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
open class NotesRepoTest {

    private lateinit var notesDao: NoteDao
    private lateinit var db: DatabaseModule

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                DatabaseModule::class.java).build()
        notesDao=db.notesDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertSavesData() {
        notesDao.insertAll(createTestCases(10))
        val result = notesDao.all.blockingObserve()
        result?.forEach {
            System.out.print(result.toString())
        }
        Assert.assertEquals(result!!.size,10)
    }

    @Test
    fun deleteTest() {
        notesDao.insertAll(createTestCases(10))
        val all= notesDao.all.blockingObserve();
        Assert.assertEquals(all?.size,10)
        val deleted=all!!.get(2)
        notesDao.delete(deleted);
        val afterResult = notesDao.all.blockingObserve()
        Assert.assertEquals(afterResult!!.size,9)
        var deletedFound=false
        afterResult?.forEach {
            if (it.createdAt==deleted.createdAt) {
                deletedFound=true
            }
        }
        Assert.assertEquals(deletedFound,false)
    }

    @Test
    fun updateTest(){
        val note=createTestCases(1).get(0);
        notesDao.insert(note)
        val item=notesDao.all.blockingObserve()!!.get(0)
        item.title="im changed"
        notesDao.update(item);
        val retrieved=notesDao.find(item.id).blockingObserve()
        Assert.assertEquals(item.title,retrieved!!.title)
    }

    fun createTestCases(count:Int):MutableList<Note>{
        val tempdata = mutableListOf<Note>()
        for (i in 1..count)
        {
            val note=Note("$i temp title $i","$i temp note $i")
            note.createdAt=System.currentTimeMillis()-(100000*i)
            note.updatedAt=System.currentTimeMillis()-(100000*i)
            tempdata.add(note)
        }
        return tempdata
    }

    @Test
    fun destroyDeletesData() {
        notesDao.insertAll(createTestCases(5))
//        notesDao.all.blockingObserve()!!.forEach {
//            notesDao.delete(it)
//        }
        notesDao.destroy()
        Assert.assertEquals(notesDao.all.blockingObserve()!!.size.toInt(),0)
    }

    @Test
    fun getRetrievesData() {
        val temp = createTestCases(10)
        temp.forEach {
            notesDao.insert(it) }

        val retrievedData = notesDao.all.blockingObserve()
        assert(retrievedData == temp.sortedWith(compareBy({ it.createdAt }, { it.createdAt })))
    }


    fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)
        val innerObserver = Observer<T> {
            value = it
            latch.countDown()
        }
        observeForever(innerObserver)
        latch.await(1, TimeUnit.SECONDS)
        return value
    }

}
