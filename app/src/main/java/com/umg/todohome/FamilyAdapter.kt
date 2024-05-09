package com.umg.todohome

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.umg.todohome.activityDataUser.Companion.uriImage

class FamilyAdapter(private val list: ArrayList<Integrants>): RecyclerView.Adapter<FamilyAdapter.MyViewHolder>(){


    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyAdapter.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_user_family, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FamilyAdapter.MyViewHolder, position: Int) {

        val family: Integrants = list[position]
            holder.name.text = family.name.toString()
            holder.rol.text = family.Rol.toString()

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
        val name: TextView = itemView.findViewById(R.id.txNameUserFamily)
        val rol: TextView = itemView.findViewById(R.id.txRolUserFamily)
        val image: ImageView = itemView.findViewById(R.id.imageUser)
    }
}