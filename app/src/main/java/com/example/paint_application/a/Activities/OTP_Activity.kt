package com.example.paint_application.a.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_application.R
import com.example.paint_application.a.Models.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit


class OTP_Activity:AppCompatActivity() {


    lateinit var btnContinue_otp : Button
    lateinit var otp_view : OtpView
    var verificationID: String = ""
    var arrlist = ArrayList<String>()

    lateinit var mAuth: FirebaseAuth
    lateinit var mFireStore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_o_t_p)

        init()
        listeners()
    }




    private fun listeners() {

        btnContinue_otp.setOnClickListener {

            val otp  = otp_view.text.toString().trim()
            if(otp.isEmpty() || (otp.length<6))
            {
                return@setOnClickListener
            }

            verifyCode(otp)
        }

    }

    private fun init() {
        var mobile_no = intent.getStringExtra("mobile")
        var name = intent.getStringExtra("name")

        otp_view= findViewById(R.id.otp_view)
        btnContinue_otp = findViewById(R.id.btnContinue_otp)

        mAuth = FirebaseAuth.getInstance()
        mFireStore= FirebaseFirestore.getInstance()

        sendVerificationCode("+91" + mobile_no)

    }

    fun sendVerificationCode(number: String) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack
        )
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationID = p0
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                var code = p0.smsCode
                if(code!=null)
                {
                    otp_view.setText(code)
                    verifyCode(code)

                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@OTP_Activity, p0.message, Toast.LENGTH_SHORT).show()
            }

        }



    fun verifyCode(code: String) {
        var credential = PhoneAuthProvider.getCredential(verificationID, code)
        signInWithCredential(credential)
    }

    fun signInWithCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //Here we will create the user and enter its credentials to the database

                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener(object :
                    OnSuccessListener<DocumentSnapshot> {
                    override fun onSuccess(data: DocumentSnapshot?) {
                        var   arr = data!!.get("imageList") as ArrayList<String?>
                        var user = User(
                            mAuth.uid.toString(), intent.getStringExtra("name"), intent.getStringExtra(
                                "mobile"),arr
                        )
                        mFireStore.collection("Users").document(mAuth.currentUser!!.uid).set(user)
                    }
                })





                val intent = Intent(this@OTP_Activity, MainActivity::class.java)

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)



            } else {
                Toast.makeText(this@OTP_Activity, "Authentication Failed......", Toast.LENGTH_SHORT).show()
            }
        }
    }




}