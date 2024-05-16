package com.umg.todohome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class AdapterTask(private val list: ArrayList<Task>): RecyclerView.Adapter<AdapterTask.MyViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterTask.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_task, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterTask.MyViewHolder, position: Int) {

        val task: Task = list[position]

        holder.title.text = task.title.toString()
        holder.descr.text = task.description.toString()
        holder.date.text = task.date.toString()
        holder.priority.text = task.importance.toString()
        holder.userName.text = task.name.toString()
        //holder.btDone.isVisible = false
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
    }
}