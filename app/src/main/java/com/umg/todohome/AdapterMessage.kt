package com.umg.todohome

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.umg.todohome.loginActivity.Companion.usermail
import layout.Message

class AdapterMessage(private val list: ArrayList<Message>): RecyclerView.Adapter<AdapterMessage.MyViewHolder>()  {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMessage.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_message, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message: Message = list[position]

        val email = message.user.toString()

        if(usermail == email){

            holder.imageUser.visibility = View.GONE
            holder.user.visibility = View.GONE
            holder.content.text = message.content.toString()
            holder.time.text = message.time.toString()

            holder.LnContent.translationX = 450F
        }else{
            holder.user.text = message.name.toString()
            holder.content.text = message.content.toString()
            holder.time.text = message.time.toString()


            val storageRef = FirebaseStorage.getInstance().getReference("fotos/$email/image/ImageUser")

            storageRef.downloadUrl
                .addOnSuccessListener { downloadUrl ->
                    Glide.with(context)
                        .load(downloadUrl)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.imageUser)
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting download URL:", exception)
                    holder.imageUser.setImageResource(R.drawable.user_image)
                    // Handle download URL retrieval failure (optional: display error message)
                }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val LnTextMessage: LinearLayout = itemView.findViewById(R.id.LnTextMessage)
        val LnContent: LinearLayout = itemView.findViewById(R.id.layoutMessageContent)
        val content: TextView = itemView.findViewById(R.id.contentMessageChat)
        val user: TextView = itemView.findViewById(R.id.txUserNameChat)
        val imageUser: ImageView = itemView.findViewById(R.id.imageUserChat)
        val time: TextView = itemView.findViewById(R.id.txTimeChat)

    }
}

       

