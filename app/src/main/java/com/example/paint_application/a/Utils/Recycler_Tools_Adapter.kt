package com.example.paint_application.Utils

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_application.R
import com.example.paint_application.a.Listeners.ToolsListener
import com.example.paint_application.a.Models.tools_model

 class Recycler_Tools_Adapter(
     val context: Context,
     val arr: ArrayList<tools_model>,
     var listener: ToolsListener
 ) : RecyclerView.Adapter<Recycler_Tools_Adapter.DashboardViewHolder>() {

     var selected = -1

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tools_items_single_item, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arr.size
    }


    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = arr[position]
         holder.tools.setImageResource(book.tools)
         holder.tools_name.text = book.tools_name
        holder.parentLayout.setOnClickListener{
            selected= position

             listener.onSelcted(book.tools_name)
            notifyDataSetChanged()
        }

        if(selected==position)
        {
            holder.tools_name.setTypeface(holder.tools_name.typeface, Typeface.BOLD)
            holder.tools_name.alpha=0.5f
        }
        else
        {
            holder.tools_name.setTypeface(Typeface.DEFAULT)
            holder.tools_name.alpha=1.0f
        }
    }

     //ViewHolder for Recycler View
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tools = view.findViewById<ImageView>(R.id.tools)
        var tools_name = view.findViewById<TextView>(R.id.tools_name)
        var parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)
    }


 }


