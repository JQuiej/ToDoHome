package com.umg.todohome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
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
import com.umg.todohome.activityAddFamily.Companion.Rol
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.loginActivity.Companion.providerSession
import com.umg.todohome.loginActivity.Companion.usermail
import kotlin.properties.Delegates

class WelcomeActivity : AppCompatActivity() {

    private var Email by Delegates.notNull<String>()
    private lateinit var mAuth: FirebaseAuth


    private var RESULT_CODE_GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        mAuth = FirebaseAuth.getInstance()
        //asignando variables a los elementos del activity

    }
    override fun onBackPressed() {
        finishAffinity()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
             inicio(currentUser.email.toString(), currentUser.providerId)
        }
    }
    fun ShowAccountCreate(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    fun GoLogin (view: View){
        Login()
    }
    private fun Login(){
        val intent = Intent(this, loginActivity::class.java)
        startActivity(intent)
    }
    fun GoGoogle(view: View){
        LoginGoogle()
    }
    private fun LoginGoogle(){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)
    }
    fun goFacebook(view: View){
        loginFacebook()
    }
     fun loginFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                result.let{
                    val token = it.accessToken
                    val credential = FacebookAuthProvider.getCredential(token.token)
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            Email = it.result.user?.email.toString()
                            inicio(Email, "Facebook")
                        }
                        else showError("Facebook")
                    }
                }
                //handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() { }
            override fun onError(error: FacebookException) { showError("Facebook") }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Email = account.email!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                mAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
                    if (task.isSuccessful)inicio(Email, "Google")
                    else Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "signInWithGoogle:failure", e)
            }
        }
    }
    private fun showError (provider: String){
        Toast.makeText(this, "Error en la conexión con $provider", Toast.LENGTH_SHORT).show()
    }
    private fun inicio(email: String, provider: String) {
        usermail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}