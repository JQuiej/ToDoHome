package com.umg.todohome


import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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


class chatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendMessage: ImageButton
    private lateinit var contentMessage: EditText
    private lateinit var MessageArrayList: ArrayList<Message>
    private lateinit var adapterMessage: AdapterMessage
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var cardView: CardView
    private lateinit var rootLayout: View

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

            if (message.isEmpty()){
                Toast.makeText(context, "No se Puede Enviar un mensaje Vacio", Toast.LENGTH_SHORT).show()
            }else {
                var collection = "message"
                var db = FirebaseFirestore.getInstance()
                db.collection(collection).document(idFamily).collection(
                    idFamily
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
        }

        Handler(Looper.getMainLooper()).postDelayed({

            val loading = view.findViewById<LinearLayout>(R.id.loadingCardMessage)

            recyclerView.visibility = View.VISIBLE
            loading.visibility = View.GONE

        }, 800)

        listenToFirestoreChanges()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        cardView = view.findViewById(R.id.cardView)
        rootLayout = view.findViewById(R.id.root_layout)
        contentMessage = view.findViewById(R.id.contentMessage)

        setupKeyboardListener()

        return view
    }
    private fun setupKeyboardListener() {
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootLayout.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootLayout.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // El teclado está visible
                val cardViewHeight = cardView.height.toFloat()
                val recyclerViewHeight = recyclerView.height
                val newTranslationY = -keypadHeight.toFloat() + cardViewHeight

                cardView.translationY = newTranslationY
                recyclerView.translationY = newTranslationY
            } else {
                // El teclado está oculto
                cardView.translationY = 0f
                recyclerView.translationY = 0f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootLayout.viewTreeObserver.removeOnGlobalLayoutListener {
        }
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
                adapterMessage.updateList(MessageArrayList)
                layoutManager = LinearLayoutManager(context)
                layoutManager.scrollToPosition( adapterMessage.itemCount - 1)
                recyclerView.layoutManager = layoutManager
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }
    }
    private fun listenToFirestoreChanges() {
        val db = FirebaseFirestore.getInstance()
        db.collection("message").document(idFamily).collection(idFamily)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                            MessageArrayList.add(dc.document.toObject(Message::class.java))
                            adapterMessage.notifyItemInserted(MessageArrayList.size - 1)
                        }
                        com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                            val updatedMessage = dc.document.toObject(Message::class.java)
                            val index = MessageArrayList.indexOfFirst { it.idMessage == updatedMessage.idMessage }
                            if (index != -1) {
                                MessageArrayList[index] = updatedMessage
                                adapterMessage.notifyItemChanged(index)
                            }
                        }
                        com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                            val removedMessage = dc.document.toObject(Message::class.java)
                            val index = MessageArrayList.indexOfFirst { it.idMessage == removedMessage.idMessage }
                            if (index != -1) {
                                MessageArrayList.removeAt(index)
                                adapterMessage.notifyItemRemoved(index)
                            }
                        }
                    }
                    layoutManager = LinearLayoutManager(context)
                    layoutManager.scrollToPosition( adapterMessage.itemCount - 1)
                    recyclerView.layoutManager = layoutManager
                }
            }
    }

    fun generateUniqueId(): String {
        val timestamp =
            SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) // Get current timestamp with millisecond precision
        return timestamp
    }
}