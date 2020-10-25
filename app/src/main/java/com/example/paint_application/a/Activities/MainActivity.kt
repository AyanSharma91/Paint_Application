package com.example.paint_application.a.Activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_application.R
import com.example.paint_application.Utils.Recycler_Tools_Adapter
import com.example.paint_application.a.Listeners.ToolsListener
import com.example.paint_application.a.Models.Constants
import com.example.paint_application.a.Models.tools_model
import com.example.paint_application.a.views.Custom_Paint_View
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ToolsListener {

    //declaring the UI
    lateinit var recycler_view : RecyclerView
    lateinit var adapter : Recycler_Tools_Adapter
    lateinit var layout_manager : LinearLayoutManager
    lateinit var paint_view : Custom_Paint_View

    lateinit var log_out_button : ImageButton
    lateinit var home_button : ImageButton
    lateinit var save_button :ImageButton

    //Firebase variables
    lateinit var storage : StorageReference
    lateinit var firestoreRefrence : DocumentReference
    var list = ArrayList<tools_model>()

    //Constants
    var REQUEST_PERMISSION = 1001


    // Paint_Variables
        var colorBackground : Int?=0
        var colorBrush : Int?=0
        var brushSize : Int?=0
        var eraserSize : Int?=0



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializtion
        init()
        //Initialize the ArrayList for Recycler_View
        toolsLoader()
        //listeners
        listeners()

        adapter= Recycler_Tools_Adapter(this@MainActivity, list, this)
        recycler_view.adapter= adapter
        recycler_view.layoutManager= layout_manager

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun listeners() {
        log_out_button.setOnClickListener{

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        home_button.setOnClickListener{
            val intent = Intent(this@MainActivity, Paint_Gallery::class.java)
            startActivity(intent)
        }
        save_button.setOnClickListener{

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUEST_PERMISSION)
            }else
            {
                saveBitmap()

            }
        }
    }

    private fun toolsLoader() {

        list.add(tools_model(R.drawable.brush, Constants().BRUSH))
        list.add(tools_model(R.drawable.eraser, Constants().ERASER))
        list.add(tools_model(R.drawable.color, Constants().COLORS))
        list.add(tools_model(R.drawable.white_image, Constants().BACKGROUND))
        list.add(tools_model(R.drawable.return_back, Constants().RETURN))
    }

    private fun init() {

        recycler_view= findViewById(R.id.recycler_view)
        paint_view= findViewById(R.id.paint_view)
        layout_manager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        storage = FirebaseStorage.getInstance().reference
        log_out_button = findViewById(R.id.log_out_button)
        home_button= findViewById(R.id.home_button)
        save_button= findViewById(R.id.save_button)

        firestoreRefrence=  FirebaseFirestore.getInstance().collection("Users"). document(FirebaseAuth.getInstance().currentUser!!.uid)

        colorBackground= Color.WHITE
        colorBrush= Color.BLACK
        eraserSize=12
        brushSize=12
    }

    private fun saveBitmap() {
        val bitmap = paint_view.getBitmap()
        val file_name = UUID.randomUUID().toString()+ ".png"
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+File.separator+ getString(
            R.string.app_name
        ))

        if(!folder.exists())
        {
            folder.mkdir()
        }

        try {
            val fileOutputStream = FileOutputStream(folder.toString() + File.separator + file_name)


              val firebase_uri = (folder.toString() + File.separator + file_name)
             firebaseUpload(firebase_uri,file_name)
             bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)
             Toast.makeText(this@MainActivity, "Picture Saved",Toast.LENGTH_LONG).show()


        }catch (e: FileNotFoundException)
        {
            e.printStackTrace()
        }





    }

    private fun firebaseUpload(firebaseUri: String,file_name : String) {
          var file = Uri.fromFile(File(firebaseUri))
          storage.child(file_name).putFile(file).addOnSuccessListener (object : OnSuccessListener<UploadTask.TaskSnapshot>{
              override fun onSuccess(download: UploadTask.TaskSnapshot?) {
                  storage.child(file_name).downloadUrl.addOnCompleteListener{
                      task ->  if(task.isComplete){
                      Log.d("DownloadUri", task.result.toString())

                      firestoreRefrence.get().addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot>{
                          override fun onSuccess(data: DocumentSnapshot?) {
                            val arr : ArrayList<String?> = data!!.get("imageList") as ArrayList<String?>
                              arr.add(task.result.toString())

                              firestoreRefrence.update("imageList",arr)
                          }
                      }).addOnFailureListener(object  : OnFailureListener{
                          override fun onFailure(p0: Exception) {

                          }
                      })


                  }

                  }


              }


          } ).addOnFailureListener(object : OnFailureListener{
              override fun onFailure(p0: Exception) {
                  Toast.makeText(this@MainActivity,"Failed",Toast.LENGTH_LONG).show()
              }
          })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode== REQUEST_PERMISSION && grantResults.size >=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            saveBitmap()
        }
    }

    override fun onSelcted(name: String) {

        when(name){

            Constants().BRUSH-> { paint_view.disableEraser()

            showDialogSize(false)}

            Constants().ERASER ->{

                paint_view.enableEraser()
                showDialogSize(true)
            }

            Constants().RETURN ->{

                paint_view.returnLastAction()
            }
            Constants().BACKGROUND ->
            {

                updateColour(name)
            }

            Constants().COLORS ->
            {
                updateColour(name)
            }



        }
    }

    private fun updateColour(name: String) {

        var color : Int?=0
        if(name.equals(Constants().BACKGROUND))
        {
             color = colorBackground
        }else
        {
            color= colorBrush
        }

        ColorPickerDialogBuilder.with(this@MainActivity)
            .setTitle("Choose Color")
            .initialColor(color!!)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("OK", object  : ColorPickerClickListener{
                override fun onClick(
                    d: DialogInterface?,
                    lastSelectedColor: Int,
                    allColors: Array<out Int>?
                ) {
                    if(name.equals(Constants().BACKGROUND))
                    {
                        colorBackground= lastSelectedColor
                        paint_view.setColorBackground(colorBackground!!)
                    }
                    else
                    {
                        colorBrush=lastSelectedColor
                        paint_view.setBrushColor(colorBrush!!)
                    }
                }
            }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {

                }
            }).build().show()


    }

    private fun showDialogSize(isEraser: Boolean) {

        var builder = AlertDialog.Builder(this@MainActivity)
        var view  = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_dialog,null,false)
        var toolsSelected = view.findViewById<TextView>(R.id.status_selected_toolbar)
        var statusSize = view.findViewById<TextView>(R.id.status_size)
        var ex_tools = view.findViewById<ImageView>(R.id.ex_tools)
        var seekbar = view.findViewById<SeekBar>(R.id.seekbar_size)
        seekbar.max=99

        if(isEraser)
        {
            toolsSelected.setText("Eraser Size")
            ex_tools.setImageResource(R.drawable.blacks_eraser)
            statusSize.setText("Selected Size : " + eraserSize)
        }
        else
        {
            toolsSelected.setText("Brush Size")
            ex_tools.setImageResource(R.drawable.black_brush)
            statusSize.setText("Selected Size : " + brushSize)
        }


        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if(isEraser)
                {
                    eraserSize= progress+1
                    statusSize.setText("Selected Size : " + eraserSize)
                    paint_view.setSizeEraser(eraserSize!!)
                }

                else
                {
                    brushSize= progress+1
                    statusSize.setText("Selected Size : " + brushSize)
                    paint_view.setSizeBrush(brushSize!!)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        builder.setPositiveButton("OK",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, count: Int) {
                dialog!!.dismiss()
            }
        })

        builder.setView(view)
        builder.show()

    }


    override fun onBackPressed() {
        finishAffinity()
    }
}