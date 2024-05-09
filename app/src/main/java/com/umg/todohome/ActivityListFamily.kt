package com.umg.todohome

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.loginActivity.Companion.usermail

class ActivityListFamily: AppCompatActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var familyArrayList: ArrayList<Integrants>
    private lateinit var AdapterFamily: FamilyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_family)

        recyclerView = findViewById(R.id.rvFamily)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        familyArrayList = arrayListOf()
        AdapterFamily = FamilyAdapter(familyArrayList)
        recyclerView.adapter = AdapterFamily

    }
    fun ExitFamily(view: View){
        //ExitUserFamily()
    }
    override fun onResume() {
        super.onResume()
        loadRecycleView()
    }
    override fun onPause() {
        super.onPause()
        familyArrayList.clear()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView() {
      familyArrayList.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("Family").document("users").collection(idFamily)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    familyArrayList.add(doc.toObject(Integrants::class.java))
                    AdapterFamily.notifyDataSetChanged()
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }
}