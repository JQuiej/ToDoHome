package com.umg.todohome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.loginActivity.Companion.providerSession
import com.umg.todohome.loginActivity.Companion.usermail
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity(){

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private var passwordConfirm by Delegates.notNull<String>()
    private lateinit var txEmail: EditText
    private lateinit var txPassword: EditText
    private lateinit var txPasswordConfirm: EditText

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acount)

        mAuth = FirebaseAuth.getInstance()

        txEmail = findViewById(R.id.txEmail)
        txPassword = findViewById(R.id.txPassword)
        txPasswordConfirm = findViewById(R.id.txPasswordConfirm)


    }
    override fun onBackPressed() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
    fun showRegisterUser(view: View){
        register()
    }
    private fun register(){

        val acepTerms = findViewById<CheckBox>(R.id.cbAcept)
        email = txEmail.text.toString()
        password = txPassword.text.toString()
        passwordConfirm = txPasswordConfirm.text.toString()

            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Falta ingresar Datos", Toast.LENGTH_LONG).show()
            }else {
                if (acepTerms.isChecked) {
                    if (password.equals(passwordConfirm)) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                                    val dbRegister = FirebaseFirestore.getInstance()
                                    dbRegister.collection("users").document(email).set(
                                        hashMapOf(
                                            "user" to email,
                                            "dateRegister" to dateRegister
                                        )
                                    )

                                    inicio(email, "email")
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Error al crear el Usuario",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "las Contraseñas ingresadas no coinciden",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Acepte los Terminos y Condiciones", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


    private fun inicio(email: String, provider: String){
        usermail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun GoLoginReg(view: View){
        val intent = Intent(this, loginActivity::class.java)
        startActivity(intent)
    }
    fun goTerms(){

    }

}