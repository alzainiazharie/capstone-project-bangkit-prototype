package com.bangkit.dermaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class FirstScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        Handler(mainLooper).postDelayed({
            val intentMain = Intent(this@FirstScreenActivity, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }, 2000)
    }
}