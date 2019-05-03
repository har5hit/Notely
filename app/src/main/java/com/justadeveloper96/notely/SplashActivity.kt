package com.justadeveloper96.notely

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.justadeveloper96.notely.list.ListActivity

/**
 * Created by harshith on 07-03-2018.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        },500)
    }
}
