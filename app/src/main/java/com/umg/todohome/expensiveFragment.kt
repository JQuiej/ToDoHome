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
        loadRecycleView()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView() {
        expenseArrayList.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("expenses").document(activityAddFamily.idFamily)
            .collection(activityAddFamily.idFamily)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    expenseArrayList.add(doc.toObject(Expense::class.java))
                    adapterExpense.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }
     fun updateTotals(){
         val totalFormateadoStringFormat = String.format("%.2f", totalEx)
         println("Total formateado con String.format(): $totalFormateadoStringFormat")

         total.setText("Q${totalFormateadoStringFormat}")
    }

}