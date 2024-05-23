package com.umg.todohome

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.loginActivity.Companion.usermail

class taskFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskArraylist: ArrayList<Task>
    private lateinit var adapterTask: AdapterTask
    private lateinit var btAddTask: FloatingActionButton
    private lateinit var btDoneTask: Button
    private lateinit var btPendTask: Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btAddTask = view.findViewById(R.id.btAddTask)
        btDoneTask = view.findViewById(R.id.taskDone)
        btPendTask = view.findViewById(R.id.taskPendin)

        recyclerView = view.findViewById(R.id.rvTaskFamily)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        taskArraylist = arrayListOf()
        adapterTask = AdapterTask(taskArraylist)
        recyclerView.adapter = adapterTask


        var loading = view.findViewById<LinearLayout>(R.id.loadingCardTask)

        btAddTask.setOnClickListener{
            val intent = Intent(requireActivity(), ActivityAddTask::class.java)
            startActivity(intent)
        }
        btDoneTask.setOnClickListener{
            loadRecycleView("Realizado")
            recyclerView.visibility = View.GONE
            loading.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                loading.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

            }, 800)
        }

        btPendTask.setOnClickListener{
            loadRecycleView("Pendiente")
            recyclerView.visibility = View.GONE
            loading.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                loading.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

            }, 800)
        }

        Handler(Looper.getMainLooper()).postDelayed({

            recyclerView.visibility = View.VISIBLE
            loading.visibility = View.GONE

        }, 500)

        listenToFirestoreChanges()

    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)

    }

    override fun onResume() {
        super.onResume()
        loadRecycleView("Pendiente")

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView(status: String) {
        taskArraylist.clear()

        val dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("task").document(idFamily).collection(idFamily).whereEqualTo("status", status)
            .orderBy("importance", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    taskArraylist.add(doc.toObject(Task::class.java))
                }
                adapterTask.updateList(taskArraylist)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }
    private fun listenToFirestoreChanges() {
        val db = FirebaseFirestore.getInstance()
        db.collection("task").document(idFamily).collection(idFamily)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                            taskArraylist.add(dc.document.toObject(Task::class.java))
                            adapterTask.notifyItemInserted(taskArraylist.size - 1)
                        }
                        com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                            val updatedTask = dc.document.toObject(Task::class.java)
                            val index = taskArraylist.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                taskArraylist[index] = updatedTask
                                adapterTask.notifyItemChanged(index)
                            }
                        }
                        com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                            val removedTask = dc.document.toObject(Task::class.java)
                            val index = taskArraylist.indexOfFirst { it.id == removedTask.id }
                            if (index != -1) {
                                taskArraylist.removeAt(index)
                                adapterTask.notifyItemRemoved(index)
                            }
                        }
                    }
                }
            }
    }

}