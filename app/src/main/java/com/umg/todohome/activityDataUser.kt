package com.umg.todohome

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.umg.todohome.activityAddFamily.Companion.Rol
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.loginActivity.Companion.usermail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FileDownloadTask

class activityDataUser: AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    companion object{
        var userName: String? = null
        var userDate: String? = null
        var userAddres: String? = null
        var uriImage: String? = null
    }

    lateinit var name: EditText
    lateinit var date: EditText
    lateinit var addres: EditText
    lateinit var Image: ImageView
    lateinit var mStorage: StorageReference

    private val GALLERY_INTENT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_user)

        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference

        Image = findViewById(R.id.imageUser)
        name = findViewById(R.id.user_Name)
        date = findViewById(R.id.User_date)
        addres = findViewById(R.id.User_addres)


        loadDataUser()
        loadImage()
    }
    fun SavePhotos(view: View){
        saveImage()
    }

    override fun onResume() {
        super.onResume()
        loadImage()
    }
    private fun saveImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            val imageUri = data?.data ?: return // Handle case where no image is selected

            val storageRef = FirebaseStorage.getInstance().getReference("fotos/$usermail/image/ImageUser")

            uriImage = "fotos/$usermail/image/ImageUser"

            Toast.makeText(this, "Su foto se esta subiendo", Toast.LENGTH_SHORT).show()

            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "Se subio exitosamente la foto.", Toast.LENGTH_SHORT).show()
                    dataUser()
                }
                .addOnFailureListener { exception ->
                    // Handle upload failure
                    Log.w("TAG", "Error uploading image:", exception)
                }
            loadImage()
        }
    }
    fun SaveDataUser(view: View){
        dataUser()
    }
    private fun dataUser() {
        userName = name.text.toString()
        userDate = date.text.toString()
        userAddres = addres.text.toString()

        var collection = "Family"
        var db = FirebaseFirestore.getInstance()
        db.collection(collection).document("users").collection(idFamily).document(
            usermail).set(
            hashMapOf(
                "user" to usermail,
                "name" to userName,
                "IdFamily" to idFamily,
                "Rol" to Rol,
                "date" to userDate,
                "addres" to userAddres,
                "uriImage" to uriImage
            )
        )
        db.collection("users").document(usermail).set(
            hashMapOf(
                "user" to usermail,
                "name" to userName,
                "IdFamily" to idFamily,
                "Rol" to Rol,
                "date" to userDate,
                "addres" to userAddres,
                "uriImage" to uriImage
            )
        )
        Toast.makeText(this, "Datos Guardados", Toast.LENGTH_SHORT).show()
        loadImage()
    }

    private fun loadDataUser() {
            var db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            val documents = try {
                db.collection("users").whereEqualTo("user", usermail).get().await()
            } catch (e: Exception) {
                null
            }

            // Update UI elements on the main thread
            withContext(Dispatchers.Main) {
                if (documents != null) {
                    for (doc in documents) {
                        userName = doc.getString("name").toString()
                        userDate = doc.getString("date").toString()
                        userAddres = doc.getString("addres").toString()
                    }
                    if(userName == "null" || userDate == "null" || userAddres == "null"){
                        name.setText("")
                        date.setText("")
                        addres.setText("")
                    }else{
                        name.setText(userName)
                        date.setText(userDate)
                        addres.setText(userAddres)
                    }
                }
            }
            loadImage()
        }
    }
    private fun loadImage() {
        val storageRef = FirebaseStorage.getInstance().getReference("$uriImage")

        storageRef.downloadUrl
            .addOnSuccessListener { downloadUrl ->
                Glide.with(this)
                    .load(downloadUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(Image)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting download URL:", exception)
                // Handle download URL retrieval failure (optional: display error message)
            }
    }
    fun DeletePhoto(view: View){
        deleteImage()
    }
    private fun deleteImage() {

    }


}
