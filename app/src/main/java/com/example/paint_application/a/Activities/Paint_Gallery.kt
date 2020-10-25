package com.example.paint_application.a.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_application.R
import com.example.paint_application.a.Utils.Paint_Gallery_Adapter
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.io.File

class Paint_Gallery : AppCompatActivity() {


    lateinit var recycler_view_paint_gallery : RecyclerView
    lateinit var adapter  : Paint_Gallery_Adapter
    lateinit var layoutManager: GridLayoutManager
    lateinit var empty_status : TextView
    lateinit var back_button : ImageView
    lateinit var progress_bar : ProgressBar
    lateinit var progress_layout : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint__gallery)

        empty_status= findViewById(R.id.empty_status)
        back_button= findViewById(R.id.back_button)
        progress_bar= findViewById(R.id.progress_bar)
        progress_layout= findViewById(R.id.progress_layout)


        empty_status.visibility= View.GONE
        progress_layout.visibility= View.VISIBLE
        progress_bar.visibility= View.VISIBLE




        RecyclerView_Setup()
        back_button.setOnClickListener{
            finish()
        }
    }

    private fun RecyclerView_Setup() {

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot>{
            override fun onSuccess(data: DocumentSnapshot?) {
                var arr : ArrayList<String>? = data!!.get("imageList") as ArrayList<String>?

                if(arr==null)
                {
                    empty_status= findViewById(R.id.empty_status)
                    empty_status.visibility= View.VISIBLE
                    progress_layout.visibility= View.GONE
                    progress_bar.visibility= View.GONE
                }
                else
                {

                    recycler_view_paint_gallery= findViewById(R.id.recycler_view_paint_gallery)
                    recycler_view_paint_gallery.setHasFixedSize(true)
                    adapter = Paint_Gallery_Adapter(this@Paint_Gallery,arr)
                    layoutManager= GridLayoutManager(this@Paint_Gallery,3)
                    recycler_view_paint_gallery.adapter=adapter
                    recycler_view_paint_gallery.layoutManager= layoutManager
                    progress_layout.visibility= View.GONE
                    progress_bar.visibility= View.GONE

                }


            }
        })
    }


}