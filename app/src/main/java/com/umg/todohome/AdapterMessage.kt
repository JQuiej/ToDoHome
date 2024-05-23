package com.umg.todohome

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.loginActivity.Companion.usermail
import layout.Message

class AdapterMessage(private var list: ArrayList<Message>): RecyclerView.Adapter<AdapterMessage.MyViewHolder>()  {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMessage.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_message, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message: Message = list[position]
        val id = message.idMessage ?: ""
        val email = message.user ?: ""

        if (usermail.equals(email, ignoreCase = true)) {
            holder.imageUser.visibility = View.GONE
            holder.user.visibility = View.GONE
            holder.content.text = message.content
            holder.time.text = message.time
            holder.LnContent.translationX = 450F

            holder.LnTextMessage.setOnLongClickListener {
                showLongClickDialog(id)
                true
            }
        } else {
            holder.user.text = message.name ?: ""
            holder.content.text = message.content ?: ""
            holder.time.text = message.time ?: ""
            holder.imageUser.visibility = View.VISIBLE
            holder.user.visibility = View.VISIBLE
            holder.LnContent.translationX = 0F

            holder.LnTextMessage.setOnLongClickListener {
                false
            }

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
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<Message>) {
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(position: Int, message: Message) {
        list[position] = message
        notifyItemChanged(position)
    }
    private fun showLongClickDialog(id: String) {
        AlertDialog.Builder(context, R.style.WhiteAlertDialogTheme)
            .setMessage("Deseas Eliminar Tu mensaje?")
            .setPositiveButton("Eliminar") { dialog, which ->
                RemoveMessage(id)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
    private fun RemoveMessage(idMessage: String) {
        val db = FirebaseFirestore.getInstance()
        val collection = "message"
        db.collection(collection).document(idFamily).collection(idFamily).document(idMessage).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Mensaje Eliminado Correctamente", Toast.LENGTH_SHORT).show()
                val position = list.indexOfFirst { it.idMessage == idMessage }
                if (position != -1) {
                    removeItem(position)
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

       

