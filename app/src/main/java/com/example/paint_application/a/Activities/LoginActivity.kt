package com.example.paint_application.a.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_application.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {


    //declaring the UI
   lateinit var  mobile_no : TextInputEditText
   lateinit var first_name : TextInputEditText
   lateinit var last_name : TextInputEditText
   lateinit var btnContinue_login : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialize the views
          init()
        // setting the listeners
          listeners()


    }

    private fun listeners() {
        btnContinue_login.setOnClickListener {

            var intent = Intent(this@LoginActivity , OTP_Activity::class.java)

            intent.putExtra("mobile",mobile_no.text.toString())
            intent.putExtra("name", first_name.text.toString()+ " "+ last_name.text.toString())
            startActivity(intent)
        }
    }

    private fun init() {
        mobile_no= findViewById(R.id.mobile_no)
        first_name= findViewById(R.id.first_name)
        last_name= findViewById(R.id.last_name)
        btnContinue_login= findViewById(R.id.btnContinue_login)

    }

    override fun onStart() {
        super.onStart()
           if(FirebaseAuth.getInstance().currentUser != null)
           {
               var intent = Intent(this@LoginActivity, MainActivity::class.java)
               startActivity(intent)

           }

    }

    override fun onBackPressed() {
        finishAffinity()
    }



}