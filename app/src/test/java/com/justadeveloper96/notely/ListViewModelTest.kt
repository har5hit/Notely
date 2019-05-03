package com.justadeveloper96.notely

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.justadeveloper96.notely.repo.Note
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import android.arch.core.executor.testing.InstantTaskExecutorRule

/**
 * Created by user on 3/8/2018.
 */
open class ListViewModelTest{

    lateinit var filterer:MediatorLiveData<List<Note>>;
    lateinit var dbSource:MutableLiveData<List<Note>>;

    @get:Rule
    public val instantrule= InstantTaskExecutorRule()

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

    lateinit var dummyDbData:List<Note>

    var filter_like_applied=false;
    var filter_star_applied=false;

    @Before
    fun initdb(){
        dummyDbData=createTestCases(10)
        dbSource=MutableLiveData()
        filterer= MediatorLiveData<List<Note>>();
    }


    fun onDbUpdates(like_applied:Boolean=false,star_applied:Boolean=false){
        filterer.removeSource(dbSource)
        filterer.addSource(dbSource,{
            filterer.value=it
            var res=it;
            if (like_applied)
            {
                res=res?.filter { it.liked }
            }
            if (star_applied)
            {
                res=res?.filter { it.starred }
            }
            filterer.value=res
        })
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

    @Test
    fun getAllTest(){
        dbSource.value = dummyDbData;
        onDbUpdates()
        Assert.assertEquals(filterer.blockingObserve()!!.size,dummyDbData.size)
    }

    @Test
    fun applyLikeFilterTest(){
        dbSource.value = dummyDbData;
        onDbUpdates(star_applied = true)
        var valid=true;
        filterer.blockingObserve()!!.forEach {
            if (!it.liked) valid=false
        }
        Assert.assertEquals(valid,true)
    }

    @Test
    fun applyStarFilterTest(){
        dbSource.postValue(dummyDbData) ;
        onDbUpdates(star_applied = true)
        var valid=true;
        filterer.blockingObserve()!!.forEach {
            if (!it.starred) valid=false
        }
        Assert.assertEquals(valid,true)
    }

    @Test
    fun refreshOnDeleteTest(){
        val dummy=createTestCases(5);
        dbSource.value=dummy
        onDbUpdates()
        dummy.removeAt(1)
        dbSource.value=dummy
        onDbUpdates()
        Assert.assertEquals(filterer.blockingObserve()!!.size,dummy.size)
    }

    @Test
    fun refreshOnInsertTest(){
        val dummy=createTestCases(5);
        dbSource.value=dummy
        onDbUpdates()
        dummy.add(createTestCases(1).get(0))
        dbSource.value=dummy
        onDbUpdates()
        Assert.assertEquals(filterer.blockingObserve()!!.size,dummy.size)
    }
}