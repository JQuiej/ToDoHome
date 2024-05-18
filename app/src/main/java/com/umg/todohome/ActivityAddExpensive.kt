package com.umg.todohome

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userName
import com.umg.todohome.expensiveFragment.Companion.totalEx
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

class ActivityAddExpensive: AppCompatActivity() {

    private lateinit var descrip: EditText
    private lateinit var categoria: EditText
    private lateinit var cantidad: EditText

    var numExpensive : Int = 0;

    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acttivity_add_expensive)

        initToolBar()
    }
    private fun initToolBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout_expensive)
        val toggle = ActionBarDrawerToggle(
            this, drawer, null, R.string.AddFamily, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }
    fun addExpensive(view: View){
        updateExpensive()
    }
    private fun updateExpensive(){

        descrip = findViewById(R.id.txdescripExpensive)
        categoria =findViewById(R.id.txcategoriaExpensive)
        cantidad = findViewById(R.id.txExpensive)

        numExpensive++

        val idExpense = generateUniqueId()
        val ExpenDesc = descrip.text.toString()
        val ExpenCat = categoria.text.toString()
        val ExpenCant = cantidad.text.toString()


        val dateExpense= SimpleDateFormat("dd/MM/yyyy").format(Date())

        var collection = "expenses"
        var db = FirebaseFirestore.getInstance()
        db.collection(collection).document(idFamily).collection(idFamily).document(idExpense).set(
            hashMapOf(
                "user" to userName,
                "description" to ExpenDesc,
                "category" to ExpenCat,
                "Expensive" to ExpenCant,
                "date" to dateExpense
            )
        )

        Toast.makeText(this, "Gasto agregado exitosamente ", Toast.LENGTH_LONG).show()
        onBackPressed()
    }

    fun generateUniqueId(): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) // Get current timestamp with millisecond precision
        val randomSuffix = Random.nextInt(10000) // Generate a random 4-digit suffix
        return "$timestamp-$randomSuffix" // Combine timestamp and random suffix
    }
}