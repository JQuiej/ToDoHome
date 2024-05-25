package com.umg.todohome

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.umg.todohome.activityAddFamily.Companion.Rol
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userAddres
import com.umg.todohome.activityDataUser.Companion.userDate
import com.umg.todohome.activityDataUser.Companion.userName
import com.umg.todohome.databinding.ActivityMainBinding
import com.umg.todohome.loginActivity.Companion.providerSession
import com.umg.todohome.loginActivity.Companion.usermail
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawer: DrawerLayout
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageUserbar: ImageView

    private val CODIGO_PERMISO_SEGUNDO_PLANO = 100
    private var isPermisos = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    lateinit var name: EditText
    lateinit var date: EditText


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imageViewLoading.visibility = View.VISIBLE

        Glide.with(this)
            .load(R.raw.loading)
            .into(binding.imageViewLoading)

        verifyFamily(usermail)
        initToolBar()
        initNavigationView()

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, toolbar,
            R.string.navigation_drawer_close, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_location -> openFragment(locationFragment())
                R.id.nav_chat -> openFragment(chatFragment())
                R.id.nav_task -> openFragment(taskFragment())
                R.id.nav_expense -> openFragment(expensiveFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager

        Handler(Looper.getMainLooper()).postDelayed({

            binding.imageViewLoading.visibility = View.GONE
            requestNotificationPermission()
            isNotificationPermissionGranted()
            openFragment(locationFragment())

        }, 3000)


    }
    override fun onResume() {
        super.onResume()
        loadImage()
        verifyFamily(usermail)
    }
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ir a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_PERMISO_SEGUNDO_PLANO
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            Toast.makeText(this, "Ir a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                CODIGO_PERMISO_SEGUNDO_PLANO
            )
        }
    }
    private fun onPermisosConcedidos() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    uploadlocation(it)
                } else {
                    Toast.makeText(this, "No se puede obtener la ubicacion", Toast.LENGTH_SHORT).show()
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                180000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    for (location in p0.locations) {
                        uploadlocation(location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (_: SecurityException) {

        }
    }
    private fun uploadlocation(ubicacion: Location){

        val location = "${ubicacion.latitude}, ${ubicacion.longitude}"
        val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
        val time =  LocalTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val formattedTime = timeFormatter.format(LocalTime.now())

        var collection = "Family"
        var db = FirebaseFirestore.getInstance()
        db.collection(collection).document("users").collection(idFamily).document(usermail).set(
            hashMapOf(
                "location" to location,
                "date" to "$date a las $formattedTime"
            ) , SetOptions.merge()
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CODIGO_PERMISO_SEGUNDO_PLANO) {
            val todosPermisosConcedidos = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (grantResults.isNotEmpty() && todosPermisosConcedidos) {
                isPermisos = true
                onPermisosConcedidos()
            }
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
                    .into(imageUserbar)
            }
            .addOnFailureListener { exception ->
                imageUserbar.setImageResource(R.drawable.user_image)
                Log.w("TAG", "Error getting download URL:", exception)
                // Handle download URL retrieval failure (optional: display error message)
            }
    }
    private fun verifyFamily(Email: String) {

        val db = FirebaseFirestore.getInstance()
        val referenciaDocumento = db.collection("users").document(Email)

        referenciaDocumento.get().addOnSuccessListener { documento ->
            if (documento.exists()) {
                idFamily = documento.data?.get("IdFamily").toString()
                Rol = documento.data?.get("Rol").toString()
                userName = documento.data?.get("name").toString()

                if(idFamily == "null" || Rol == "null"){
                    idFamily = ""
                    Rol = ""
                }
            }else{
                idFamily = ""
                Rol = ""
            }
        }.addOnFailureListener { excepcion ->
            println("Error al recuperar el documento: $excepcion")

        }
    }

    private fun initToolBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }
    private fun  initNavigationView(){

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val headerView: View = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        imageUserbar = headerView.findViewById(R.id.imageUserBar)
        val tvUser: TextView = headerView.findViewById(R.id.userLogin)
        tvUser.text = usermail

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_User -> goUserData()
            R.id.item_Family -> goAddFamily()
            R.id.item_logout -> alertSignout()
            R.id.item_anuncio -> ShowExpense()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        drawer.closeDrawer(GravityCompat.START)
        loadImage()
        return true
    }
    fun goAddFamily(){
        if(idFamily.isEmpty()){
            val intent = Intent(this, activityAddFamily::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, ActivityListFamily::class.java)
            startActivity(intent)
        }
    }
    private fun goUserData(){
        val intent = Intent(this, activityDataUser::class.java)
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openFragment(fragment: Fragment){
        if(fragment is fragment_welcome){
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, fragment)
            fragmentTransaction.commit()
        }else if (!idFamily.isEmpty()){
            requestLocationPermission()
            isLocationPermissionGranted()
            startNotificationWorker()
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, fragment)
            fragmentTransaction.commit()
        }else {
            alertFamily()
        }
    }
    private fun startNotificationWorker() {
        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun alertFamily(){
        AlertDialog.Builder(this, R.style.WhiteAlertDialogTheme)
            .setTitle(getString(R.string.IdFamily))
            .setMessage(R.string.noIdFamily)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    goAddFamily()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->
                    openFragment(fragment_welcome())
                })
            .setCancelable(true)
            .show()
    }
    private fun alertSignout(){
        AlertDialog.Builder(this, R.style.WhiteAlertDialogTheme)
            .setTitle(getString(R.string.titleSignOut))
            .setMessage(R.string.signOutText)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    signOut()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->
                    closeContextMenu()
                })
            .setCancelable(true)
            .show()
    }
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
           binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else finishAffinity()
    }
    private fun ShowExpense() {
        val url = "https://umg.edu.gt/ingenieria/sistemas"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)

    }
    private fun signOut(){
        usermail = " "
        idFamily = ""
        Rol = ""
        userName = ""
        userDate =""
        userAddres = ""
        imageUserbar.setImageResource(R.drawable.user_image)


        if(providerSession == "Facebook") LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, WelcomeActivity::class.java))


    }
}

