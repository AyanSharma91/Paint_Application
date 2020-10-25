package com.example.paint_application.a.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.paint_application.R

class SplashScreen: AppCompatActivity() {


    lateinit var icon_app : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash__screen_second)

        icon_app = findViewById(R.id.icon_app)
        icon_app.alpha = 0f
        icon_app.animate().alpha(1f).duration = 1000


  // separate thread for background counter time for mocking loading
        val background = object : Thread() {
                override fun run() {
                    try {
                        Thread.sleep(2500)


                        val intent = Intent(
                            this@SplashScreen,
                            LoginActivity::class.java
                        )
                        startActivity(intent)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
            background.start()
        }


    }
