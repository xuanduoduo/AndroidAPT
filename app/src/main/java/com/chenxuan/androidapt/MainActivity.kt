package com.chenxuan.androidapt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chenxuan.annotation.Hello

@Hello("MainActivity")
class MainActivity : AppCompatActivity() {
    @Hello("tag")
    lateinit var tag: String

    @Hello("onCreate")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Hello("onResume")
    override fun onResume() {
        super.onResume()
    }
}