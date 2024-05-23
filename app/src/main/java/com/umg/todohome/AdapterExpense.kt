package com.umg.todohome

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.expensiveFragment.Companion.totalEx
import com.umg.todohome.loginActivity.Companion.usermail
import java.text.DecimalFormat

class AdapterExpense(private var list: ArrayList<Expense>): RecyclerView.Adapter<AdapterExpense.MyViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterExpense.MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_expense, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: AdapterExpense.MyViewHolder, position: Int) {
        val expense: Expense = list[position]

        val id = expense.id.toString()
        var cantidad = expense.Expensive!!.toFloat().toDouble()
        val decimalFormat = DecimalFormat("Q###,###.00")
        val formattedAmount = decimalFormat.format(cantidad)

        if (usermail.equals(expense.email.toString()) ){

            holder.user.text = expense.user.toString()
            holder.descr.text = expense.description.toString()
            holder.date.text = expense.date.toString()
            holder.cate.text = expense.category.toString()
            holder.ExpenseCant.text = formattedAmount.toString()

            holder.LayoutContent.setOnLongClickListener {
                showLongClickDialog(id)
                true
            }

        }else{


        holder.LayoutContent.setOnLongClickListener {
            false
        }

        holder.user.text = expense.user.toString()
        holder.descr.text = expense.description.toString()
        holder.date.text = expense.date.toString()
        holder.cate.text = expense.category.toString()
        holder.ExpenseCant.text = formattedAmount.toString()
        }
    }
    private fun showLongClickDialog(id: String) {
        AlertDialog.Builder(context, R.style.WhiteAlertDialogTheme)
            .setMessage("Deseas Eliminar este Gasto?")
            .setPositiveButton("Eliminar") { dialog, which ->
                RemoveExpense(id)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
    private fun RemoveExpense(id: String) {
        val db = FirebaseFirestore.getInstance()
        val collection = "expenses"
        db.collection(collection).document(idFamily).collection(idFamily).document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Gasto Eliminado Correctamente", Toast.LENGTH_SHORT).show()
                // Actualizar la lista de tareas y la vista
                val position = list.indexOfFirst { it.id == id }
                if (position != -1) {
                    removeItem(position)
                }
            }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<Expense>) {
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(position: Int, expense: Expense) {
        list[position] = expense
        notifyItemChanged(position)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val LayoutContent: LinearLayout = itemView.findViewById(R.id.layoutCardExpense)
        val descr: TextView = itemView.findViewById(R.id.txdescripExpense)
        val date: TextView = itemView.findViewById(R.id.txDateExpense)
        val user: TextView = itemView.findViewById(R.id.txExpenseUser)
        val cate: TextView = itemView.findViewById(R.id.txCategoriaExpense)
        val ExpenseCant: TextView = itemView.findViewById(R.id.txCantidadExpense)

    }
}