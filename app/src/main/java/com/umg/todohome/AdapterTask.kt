package com.umg.todohome

import android.content.Context
import android.content.DialogInterface
import android.provider.Settings.Secure.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userName
import com.umg.todohome.loginActivity.Companion.usermail

class AdapterTask(private val list: ArrayList<Task>): RecyclerView.Adapter<AdapterTask.MyViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterTask.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_task, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterTask.MyViewHolder, position: Int) {

        val task: Task = list[position]

        val id = task.id.toString()
        val status = task.status.toString()
        holder.title.text = task.title.toString()
        holder.descr.text = task.description.toString()
        holder.date.text = task.date.toString()
        holder.priority.text = task.importance.toString()
        holder.userName.text = task.name.toString()

        if(status.equals("Realizado")){
            holder.btDone.isVisible = false
            holder.btRemove.isVisible = true
        }else{
            holder.btDone.isVisible = true
            holder.btRemove.isVisible = true
        }

        holder.btDone.setOnClickListener{
            alertDoneTask(id)
        }
        holder.btRemove.setOnClickListener {
            alertRemoveTask(id)
        }

    }

    private fun doneTask(id: String){
        var db = FirebaseFirestore.getInstance()
        var collection = "task"
        db.collection(collection).document(idFamily).collection(idFamily).document(id).set(
            hashMapOf(
                "status" to "Realizado",
                "name" to userName
            ) , SetOptions.merge()
        )
        Toast.makeText(context, "Marcada como realizada", Toast.LENGTH_SHORT).show()
    }
    private fun alertDoneTask(id: String){
        AlertDialog.Builder(context, R.style.WhiteAlertDialogTheme)
            .setTitle((R.string.DoneTask))
            .setMessage(R.string.textDoneTask)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    doneTask(id)
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .setCancelable(true)
            .show()
    }
    private fun alertRemoveTask(id: String){
        AlertDialog.Builder(context, R.style.WhiteAlertDialogTheme)
            .setTitle((R.string.RemoveTask))
            .setMessage(R.string.TextRemoveTask)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    RemoveTask(id)
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .setCancelable(true)
            .show()
    }
    private fun RemoveTask(id: String){
        var db = FirebaseFirestore.getInstance()
        var collection = "task"
        db.collection(collection).document(idFamily).collection(idFamily).document(id).delete()
        Toast.makeText(context, "Tarea Eliminada Correctamente", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.txtitletask)
        val descr: TextView = itemView.findViewById(R.id.txdescriptask)
        val date: TextView = itemView.findViewById(R.id.txDatetask)
        val priority: TextView = itemView.findViewById(R.id.txtRelevance)
        val userName: TextView = itemView.findViewById(R.id.txuserTask)
        val btDone: Button = itemView.findViewById(R.id.btdonetask)
        val btRemove: Button = itemView.findViewById(R.id.btClearTask)
    }
}