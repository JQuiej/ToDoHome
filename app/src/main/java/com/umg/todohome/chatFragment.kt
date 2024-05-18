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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userName
import com.umg.todohome.loginActivity.Companion.usermail
import layout.Message
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.random.Random

class chatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendMessage: ImageButton
    private lateinit var contentMessage: EditText
    private lateinit var MessageArrayList: ArrayList<Message>
    private lateinit var adapterMessage: AdapterMessage
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loading = view.findViewById<LinearLayout>(R.id.loadingCardMessage)
        sendMessage = view.findViewById(R.id.SendButtonMessage)
        contentMessage = view.findViewById(R.id.contentMessage)

        recyclerView = view.findViewById(R.id.recycleVewChat)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        MessageArrayList = arrayListOf()
        adapterMessage = AdapterMessage(MessageArrayList)
        recyclerView.adapter = adapterMessage

        sendMessage.setOnClickListener{

            val message = contentMessage.text.toString()
            val idMessage = generateUniqueId()

            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            val formattedTime = timeFormatter.format(LocalTime.now())

            var collection = "message"
            var db = FirebaseFirestore.getInstance()
            db.collection(collection).document(idFamily).collection(idFamily
            ).document(idMessage).set(
                hashMapOf(
                    "idMessage" to idMessage,
                    "user" to usermail,
                    "time" to formattedTime,
                    "name" to userName,
                    "content" to message,
                )
            )
            recyclerView.visibility = View.GONE
            loading.visibility = View.VISIBLE
            contentMessage.setText("")

            loadRecycleView()
            Handler(Looper.getMainLooper()).postDelayed({

                recyclerView.visibility = View.VISIBLE
                loading.visibility = View.GONE

            }, 800)

        }

        Handler(Looper.getMainLooper()).postDelayed({

            val loading = view.findViewById<LinearLayout>(R.id.loadingCardMessage)

            recyclerView.visibility = View.VISIBLE
            loading.visibility = View.GONE

        }, 800)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
    override fun onResume() {
        super.onResume()
        loadRecycleView()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecycleView() {
        MessageArrayList.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("message").document(idFamily).collection(idFamily)
            .orderBy("idMessage", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    MessageArrayList.add(doc.toObject(Message::class.java))
                }
                adapterMessage.notifyDataSetChanged()
                layoutManager = LinearLayoutManager(context)
                layoutManager.scrollToPosition( adapterMessage.itemCount - 1)
                recyclerView.layoutManager = layoutManager
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }

    fun generateUniqueId(): String {
        val timestamp =
            SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) // Get current timestamp with millisecond precision

        return timestamp
    }
}