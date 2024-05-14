package com.umg.todohome

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umg.todohome.activityAddFamily.Companion.idFamily

class taskFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskArraylist: ArrayList<Task>
    private lateinit var adapterTask: AdapterTask
    private lateinit var btAddTask: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btAddTask = view.findViewById(R.id.btAddTask)

        recyclerView = view.findViewById(R.id.rvTaskFamily)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        taskArraylist = arrayListOf()
        adapterTask = AdapterTask(taskArraylist)
        recyclerView.adapter = adapterTask

        btAddTask.setOnClickListener{
            val intent = Intent(requireActivity(), ActivityAddTask::class.java)
            startActivity(intent)
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)

    }

    override fun onResume() {
        super.onResume()
        loadRecycleView()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView() {
        taskArraylist.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("task").document(idFamily).collection(idFamily)
            .orderBy("importance",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    taskArraylist.add(doc.toObject(Task::class.java))
                    adapterTask.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }

}