package com.umg.todohome

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily


class locationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var familyArrayList: ArrayList<Integrants>
    private lateinit var AdapterFamily: AdapterFamilyLocation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.rvFamilyLocation)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        familyArrayList = arrayListOf()
        AdapterFamily = AdapterFamilyLocation(familyArrayList)
        recyclerView.adapter = AdapterFamily
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)

    }
    override fun onResume() {
        super.onResume()
        loadRecycleView()
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