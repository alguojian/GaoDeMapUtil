package com.alguojian.gaodemaputil

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alguojian.map.BaseMapActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseMapActivity.start(this)
        Log.d("alguojian", ShaOne.sha1(this))
    }
}
