package com.umg.todohome

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FileDownloadTask
import java.util.Calendar

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
    lateinit var imageViewLoading: ImageView
    lateinit var lnViewData: LinearLayout
    lateinit var Image: ImageView
    lateinit var mStorage: StorageReference

    private val GALLERY_INTENT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_user)

        lnViewData = findViewById(R.id.lnUserData)

        imageViewLoading = findViewById(R.id.loadingImageUser)


        Glide.with(this)
            .load(R.raw.loading)
            .into(imageViewLoading)

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
            lnViewData.visibility = View.GONE
            imageViewLoading.visibility = View.VISIBLE

            val imageUri = data?.data ?: return // Handle case where no image is selected

            val storageRef = FirebaseStorage.getInstance().getReference("fotos/$usermail/image/ImageUser")

            uriImage = "fotos/$usermail/image/ImageUser"

            Toast.makeText(this, "Su foto se esta subiendo", Toast.LENGTH_SHORT).show()

            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        lnViewData.visibility = View.VISIBLE
                        imageViewLoading.visibility = View.GONE
                        Toast.makeText(this, "Se subio exitosamente la foto.", Toast.LENGTH_SHORT).show()
                        dataUser()
                    }, 800)

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

        var db = FirebaseFirestore.getInstance()
        var collection = "Family"
        db.collection(collection).document("users").collection(idFamily).document(
            usermail).set(
            hashMapOf(
                "name" to userName,
            ) , SetOptions.merge()
        )
        db.collection("users").document(usermail).set(
            hashMapOf(
                "user" to usermail,
                "name" to userName,
                "date" to userDate,
                "addres" to userAddres,
            ) , SetOptions.merge()
        )
        Toast.makeText(this, "Datos Guardados", Toast.LENGTH_SHORT).show()
        loadImage()
    }
    fun showDatePickerDialog(view: android.view.View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                date.setText(selectedDate)
                userDate = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
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
                    if(userDate == "null" || userAddres == "null"){
                        userDate = ""
                        userAddres = ""
                    }
                    name.setText(userName)
                    date.setText(userDate)
                    addres.setText(userAddres)
                }
            }
            loadImage()
        }
    }
    private fun loadImage() {
        val storageRef = FirebaseStorage.getInstance().getReference("fotos/$usermail/image/ImageUser")

        storageRef.downloadUrl
            .addOnSuccessListener { downloadUrl ->
                Glide.with(this)
                    .load(downloadUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(Image)
            }
            .addOnFailureListener { exception ->
                Image.setImageResource(R.drawable.user_image)
                Log.w("TAG", "Error getting download URL:", exception)
                // Handle download URL retrieval failure (optional: display error message)
            }
    }
    fun DeletePhoto(view: View){
        alertDeleteImage()
    }
    private fun alertDeleteImage(){
        AlertDialog.Builder(this, R.style.WhiteAlertDialogTheme)
            .setTitle(getString(R.string.titleDeleteImage))
            .setMessage(R.string.textDeleteImage)
            .setInverseBackgroundForced(true)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    deleteImage()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->
                    closeContextMenu()
                })
            .setCancelable(true)
            .show()
    }
    private fun deleteImage() {

        val storageRef = FirebaseStorage.getInstance().getReference("fotos/$usermail/image/ImageUser")

        Toast.makeText(this, "Eliminando Foto", Toast.LENGTH_SHORT).show()


        storageRef.delete()
            .addOnSuccessListener {
                println("Documento eliminado exitosamente")
                Toast.makeText(this, "Foto Eliminada", Toast.LENGTH_SHORT).show()
                loadImage()
            }
            .addOnFailureListener { e ->
                println("Error al eliminar documento: ${e.message}")
            }

    }

}
