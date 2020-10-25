package com.example.paint_application.a.Utils

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_application.R
import com.squareup.picasso.Picasso
import java.io.File

class Paint_Gallery_Adapter(val context: Context, val arr: ArrayList<String?>) : RecyclerView.Adapter<Paint_Gallery_Adapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_gallery_image, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arr.size
    }


    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = arr[position]

        Picasso.get().load(book).error(R.drawable.paint_app_icon).into(holder.image)

//        holder.image.setImageURI(Uri.fromFile(book))


    }

    //ViewHolder for Recycler View
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image_view = view.findViewById<LinearLayout>(R.id.image_view)
        var image = view.findViewById<ImageView>(R.id.image)

    }
}
