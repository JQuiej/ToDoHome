package com.umg.todohome

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.loginActivity.Companion.usermail
import kotlin.properties.Delegates

class activityAddFamily: AppCompatActivity () {

    companion object {
        lateinit var idFamily: String
        lateinit var Rol: String
    }
    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family)

        initToolBar()
    }
    private fun initToolBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout_Family)
        val toggle = ActionBarDrawerToggle(
            this, drawer, null, R.string.AddFamily, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    fun ShowFamily(view: View){
        familyManager()
    }
    private fun familyManager() {
        val rbFather = findViewById<RadioButton>(R.id.radio_father)
        val rbChild = findViewById<RadioButton>(R.id.radio_child)
        val rbCreate = findViewById<RadioButton>(R.id.radio_create)
        val rbUnite = findViewById<RadioButton>(R.id.radio_unite)
        val id = findViewById<EditText>(R.id.IdFamily)

        if (rbFather.isChecked) {
            Rol = "Padre"
        } else if (rbChild.isChecked) {
            Rol = "Hijo(a)"
        } else if (!rbChild.isChecked or !rbFather.isChecked) {
            Toast.makeText(this, "Seleccione Su Rol ", Toast.LENGTH_SHORT).show()
        } else {

            idFamily = id.text.toString()

            if (rbCreate.isChecked) {
                var collection = "Family"
                var dbRun = FirebaseFirestore.getInstance()
                dbRun.collection(collection).document(idFamily).set(
                    hashMapOf(
                        "IdFamily" to idFamily,
                    )
                )
                var collectionUser = "users"
                dbRun.collection(collectionUser).document(usermail).set(
                    hashMapOf(
                        "user" to usermail,
                        "IdFamily" to idFamily,
                        "Rol" to Rol,
                    )
                )
                Toast.makeText(this, "Familia creada exitosamente", Toast.LENGTH_SHORT).show()

            } else if (rbUnite.isChecked) {
                val db = FirebaseFirestore.getInstance()
                val usersRef = db.collection("Family")

                usersRef.whereEqualTo("IdFamily", idFamily).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            if (task.result!!.isEmpty) {
                                Toast.makeText(
                                    this,
                                    "El codigo de Familia no existe",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                var collectionUser = "users"
                                db.collection(collectionUser).document(usermail).set(
                                    hashMapOf(
                                        "user" to usermail,
                                        "IdFamily" to idFamily,
                                        "Rol" to Rol,
                                    )
                                )
                                Toast.makeText(
                                    this,
                                    "Te has unido exitosamente a $idFamily",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Error al consultar la base de datos
                            Toast.makeText(
                                this,
                                "El codigo de Familia no existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else Toast.makeText(this, "hay opciones sin seleccionar", Toast.LENGTH_SHORT).show()

        }
    }
}


