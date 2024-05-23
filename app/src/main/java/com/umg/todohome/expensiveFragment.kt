package com.umg.todohome

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umg.todohome.activityAddFamily.Companion.idFamily
import java.text.DecimalFormat

class expensiveFragment : Fragment() {

    companion object{
         var totalEx = 0.00
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseArrayList: ArrayList<Expense>
    private lateinit var adapterExpense: AdapterExpense
    private lateinit var btAddExpensive: FloatingActionButton
    private lateinit var total: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btAddExpensive = view.findViewById(R.id.btAddExpensive)
        total = view.findViewById(R.id.txTotalExpensive)


        recyclerView = view.findViewById(R.id.rvFamilyExpensive)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        expenseArrayList = arrayListOf()
        adapterExpense = AdapterExpense(expenseArrayList)
        recyclerView.adapter = adapterExpense

        btAddExpensive.setOnClickListener{
            val intent = Intent(requireActivity(), ActivityAddExpensive::class.java)
            startActivity(intent)
        }

        Handler(Looper.getMainLooper()).postDelayed({

            var viewRecycle = view.findViewById<LinearLayout>(R.id.viewdataExpensive)
            var loading = view.findViewById<LinearLayout>(R.id.loadingCardExpensive)

            viewRecycle.visibility = View.VISIBLE
            loading.visibility = View.GONE
            updateTotals()

        }, 800)

        listenToFirestoreChanges()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expensive, container, false)
    }

    override fun onPause() {
        super.onPause()
        totalEx = 0.00
    }

    override fun onResume() {
        super.onResume()
        totalEx = 0.00
        loadRecycleView()
        updateTotals()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView() {
        expenseArrayList.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("expenses").document(idFamily)
            .collection(idFamily)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val expense = doc.toObject(Expense::class.java)
                    expenseArrayList.add(expense)
                }
                adapterExpense.notifyDataSetChanged()
                recalculateTotal()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }
    private fun listenToFirestoreChanges() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("expenses").document(idFamily).collection(idFamily)

        collectionRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                        val addedExpense = dc.document.toObject(Expense::class.java)
                        expenseArrayList.add(addedExpense)
                        adapterExpense.notifyItemInserted(expenseArrayList.size - 1)
                    }
                    com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                        val updatedExpense = dc.document.toObject(Expense::class.java)
                        val index = expenseArrayList.indexOfFirst { it.id == updatedExpense.id }
                        if (index != -1) {
                            expenseArrayList[index] = updatedExpense
                            adapterExpense.notifyItemChanged(index)
                        }
                    }
                    com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                        val removedExpense = dc.document.toObject(Expense::class.java)
                        val index = expenseArrayList.indexOfFirst { it.id == removedExpense.id }
                        if (index != -1) {
                            expenseArrayList.removeAt(index)
                            adapterExpense.notifyItemRemoved(index)
                        }
                    }
                }
            }
            recalculateTotal()
        }
    }

    private fun recalculateTotal() {
        totalEx = expenseArrayList.sumOf { it.Expensive!!.toFloat().toDouble() }
        updateTotals()
    }

    fun updateTotals() {
        val decimalFormat = DecimalFormat("Q###,###.00")
        val formattedAmount = decimalFormat.format(totalEx)
        total.text = formattedAmount
    }

}