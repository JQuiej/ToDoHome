package com.umg.todohome

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

class ActivityAddTask: AppCompatActivity() {

    companion object{
        var status: String? = null
        var relevance: String? = null
    }

    private lateinit var title: EditText
    private lateinit var descrip: EditText
    private lateinit var alta: RadioButton
    private lateinit var media: RadioButton
    private lateinit var poca: RadioButton

    var numTask : Int = 0;
    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        initToolBar()
    }
    private fun initToolBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout_task)
        val toggle = ActionBarDrawerToggle(
            this, drawer, null, R.string.app_task, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    fun AddTask(view: View){
        updateTask()
    }
    private fun updateTask(){
        title = findViewById(R.id.txtitle)
        descrip = findViewById(R.id.txdescrip)
        alta =findViewById(R.id.radio_Alta)
        media = findViewById(R.id.radio_media)
        poca = findViewById(R.id.radio_poca)

        numTask++

        val idTask = generateUniqueId()
        val taskT = title.text.toString()
        val taksD = descrip.text.toString()

        status = "Pendiente"
        if(alta.isChecked){
            relevance = "Alta"
        }
        if(media.isChecked){
            relevance = "Media"
        }
        if(poca.isChecked){
            relevance = "Poca"
        }

        val dateTask = SimpleDateFormat("dd/MM/yyyy").format(Date())

        var collection = "task"
        var db = FirebaseFirestore.getInstance()
        db.collection(collection).document(idFamily).collection(idFamily).document(idTask).set(
            hashMapOf(
                "title" to taskT,
                "description" to taksD,
                "importance" to relevance,
                "status" to status,
                "date" to dateTask
            )
        )

        Toast.makeText(this, "Tarea agregada exitosamente ", Toast.LENGTH_LONG).show()
        onBackPressed()
    }

    fun generateUniqueId(): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) // Get current timestamp with millisecond precision
        val randomSuffix = Random.nextInt(10000) // Generate a random 4-digit suffix
        return "$timestamp-$randomSuffix" // Combine timestamp and random suffix
    }
}