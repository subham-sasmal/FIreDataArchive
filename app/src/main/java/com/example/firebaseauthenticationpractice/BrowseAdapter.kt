package com.example.firebaseauthenticationpractice

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BrowseAdapter(
    var browsedatalist: List<BrowseData>
): RecyclerView.Adapter<BrowseAdapter.BrowseViewHolder>() {
    inner class BrowseViewHolder(browseview: View) : RecyclerView.ViewHolder(browseview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_recieved, parent, false)
        return BrowseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return browsedatalist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.browse_name).text = "Name: " + browsedatalist[position].name
            findViewById<TextView>(R.id.browse_age).text = "Age: " + browsedatalist[position].age.toString()
            findViewById<TextView>(R.id.browse_phone).text = "Phone: " + browsedatalist[position].phone.toString()
            findViewById<TextView>(R.id.browse_address).text = "Address: " + browsedatalist[position].address
        }
    }
}