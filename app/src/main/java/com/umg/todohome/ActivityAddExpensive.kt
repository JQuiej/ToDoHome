package com.umg.todohome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userName
import com.umg.todohome.expensiveFragment.Companion.totalEx
import com.umg.todohome.loginActivity.Companion.usermail
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

class ActivityAddExpensive: AppCompatActivity() {

    private lateinit var descrip: EditText
    private lateinit var categoria: EditText
    private lateinit var cantidad: EditText
    private lateinit var fragmentManager: FragmentManager


    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acttivity_add_expensive)

        fragmentManager = supportFragmentManager

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

        val idExpense = generateUniqueId()
        val ExpenDesc = descrip.text.toString()
        val ExpenCat = categoria.text.toString()
        val ExpenCant = cantidad.text.toString()

        val dateExpense = SimpleDateFormat("dd/MM/yyyy").format(Date())

        var collection = "expenses"
        var db = FirebaseFirestore.getInstance()
        db.collection(collection).document(idFamily).collection(idFamily).document(idExpense).set(
            hashMapOf(
                "email" to usermail,
                "id" to idExpense,
                "user" to userName,
                "description" to ExpenDesc,
                "category" to ExpenCat,
                "Expensive" to ExpenCant,
                "date" to dateExpense
            )
        ).addOnSuccessListener {
            Toast.makeText(this, "Gasto agregado exitosamente", Toast.LENGTH_LONG).show()
            totalEx += ExpenCant.toFloat().toDouble() // Actualizar el total después de agregar un nuevo gasto
            onBackPressed()
        }
    }

    fun generateUniqueId(): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        return timestamp
    }
}