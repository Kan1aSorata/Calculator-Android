package com.fengyuhe.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HorizontalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal)
        println("1111111")
        window.decorView.apply {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
    }
}