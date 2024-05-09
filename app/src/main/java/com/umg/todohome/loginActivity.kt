package com.umg.todohome


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.Rol
import com.umg.todohome.activityAddFamily.Companion.idFamily
import kotlin.properties.Delegates


class loginActivity : AppCompatActivity(){

    companion object {
        lateinit var usermail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var txEmail: EditText
    private lateinit var txPassword: EditText

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        txEmail = findViewById(R.id.txEmail)
        txPassword = findViewById(R.id.txPassword)

        email = txEmail.text.toString()
        password = txPassword.text.toString()


    }
    override fun onBackPressed() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
    fun ShowLoginUser(view: View){
        loginUser()
    }
    private fun loginUser(){
        email = txEmail.text.toString()
        password = txPassword.text.toString()
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese su correo y contraseña", Toast.LENGTH_LONG).show()
        }else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        inicio(email, provider = "email")
                    } else {
                        Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }
    fun showLoginUser(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun inicio(email: String, provider: String){
        usermail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun resetPassword(view: View){
        resetPasswordSend()
    }
    private fun resetPasswordSend() {
        val email = txEmail.text.toString()

        if (!TextUtils.isEmpty(email)) {
            // Consulta a Firestore para verificar el correo electrónico
            val db = FirebaseFirestore.getInstance()
            val usersRef = db.collection("users")

            usersRef.whereEqualTo("user", email).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        if (task.result!!.isEmpty) {
                            // Correo electrónico no encontrado
                            Toast.makeText(this, "No se encontró el usuario con este correo $email", Toast.LENGTH_SHORT).show()
                        } else {
                            // Correo electrónico encontrado, enviar correo de restablecimiento
                            mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Toast.makeText(this, "Correo Enviado a $email", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Error al enviar correo electrónico
                                        Toast.makeText(this, "Error al enviar correo electrónico", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        // Error al consultar la base de datos
                        Toast.makeText(this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Correo electrónico vacío
            Toast.makeText(this, "Ingresa un Correo", Toast.LENGTH_SHORT).show()
        }
    }

}

