package com.umg.todohome

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class AdapterFamilyLocation(private val list: ArrayList<Integrants>): RecyclerView.Adapter<AdapterFamilyLocation.MyViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFamilyLocation.MyViewHolder  {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_user_location, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterFamilyLocation.MyViewHolder, position: Int) {
        val family: Integrants = list[position]

        holder.name.text = family.name.toString()

        var uri = family.uriImage.toString()

        val storageRef = FirebaseStorage.getInstance().getReference("$uri")

        storageRef.downloadUrl
            .addOnSuccessListener { downloadUrl ->
                Glide.with(context)
                    .load(downloadUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.image)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting download URL:", exception)
                // Handle download URL retrieval failure (optional: display error message)
            }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txNameUserFamilylt)
        val getLocation: Button = itemView.findViewById(R.id.GetLocation)
        val image: ImageView = itemView.findViewById(R.id.imageUserlt)
    }
}