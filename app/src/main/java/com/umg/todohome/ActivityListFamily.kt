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
import com.google.firebase.firestore.SetOptions
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
        ExitUserFamily()
    }
    private fun ExitUserFamily(){
        var db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Family").document("users").collection(idFamily)
            .document(usermail)

        val PathIdFamily = db.collection("users").document(usermail)

        docRef.delete()
            .addOnSuccessListener {
                println("Documento eliminado exitosamente")
            }
            .addOnFailureListener { e ->
                println("Error al eliminar documento: ${e.message}")
            }



        // Create or update the user document with a server-side merge
        val updateMap = hashMapOf<String, Any>(
            "IdFamily" to "",
            "Rol" to ""
        )

        PathIdFamily.set(updateMap, SetOptions.merge())
            .addOnSuccessListener {
                // Update successful
                Log.d("Firestore", "Name updated successfully!")
                Toast.makeText(this, "Te has Salido de la $idFamily ", Toast.LENGTH_LONG).show()
                idFamily = ""
            }
            .addOnFailureListener { exception ->
                // Update failed (or document creation failed)
                Log.w("Firestore", "Error updating name: $exception")
            }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

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

        var db = FirebaseFirestore.getInstance()
        db.collection("Family").document("users").collection(idFamily)
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