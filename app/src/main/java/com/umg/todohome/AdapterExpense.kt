package com.umg.todohome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.umg.todohome.expensiveFragment.Companion.totalEx

class AdapterExpense(private val list: ArrayList<Expense>): RecyclerView.Adapter<AdapterExpense.MyViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterExpense.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_expense, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: AdapterExpense.MyViewHolder, position: Int) {
        val expense: Expense = list[position]

        var cantidad = expense.Expensive!!.toFloat().toDouble()

        holder.user.text = expense.user.toString()
        holder.title.text = expense.title.toString()
        holder.descr.text = expense.description.toString()
        holder.date.text = expense.date.toString()
        holder.cate.text = expense.category.toString()
        holder.ExpenseCant.text = "Q" + expense.Expensive.toString()

        totalEx += cantidad
    }
    override fun getItemCount(): Int {
        return list.size
    }
    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.txtitleExpense)
        val descr: TextView = itemView.findViewById(R.id.txdescripExpense)
        val date: TextView = itemView.findViewById(R.id.txDateExpense)
        val user: TextView = itemView.findViewById(R.id.txExpenseUser)
        val cate: TextView = itemView.findViewById(R.id.txCategoriaExpense)
        val ExpenseCant: TextView = itemView.findViewById(R.id.txCantidadExpense)

    }
}