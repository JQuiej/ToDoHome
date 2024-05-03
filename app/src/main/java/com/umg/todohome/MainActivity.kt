package com.umg.todohome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.umg.todohome.activityAddFamily.Companion.Rol
import com.umg.todohome.databinding.ActivityMainBinding
import com.umg.todohome.loginActivity.Companion.providerSession
import com.umg.todohome.loginActivity.Companion.usermail

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawer: DrawerLayout
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        openFragment(locationFragment())
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

        val tvUser: TextView = headerView.findViewById(R.id.userLogin)
        tvUser.text = usermail

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_User -> goUserData()
            R.id.item_Family -> goAddFamily()
            R.id.item_logout -> signOut()
            R.id.item_anuncio -> ShowExpense()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    fun goAddFamily(){
        val intent = Intent(this, activityAddFamily::class.java)
        startActivity(intent)
    }
    private fun goUserData(){
        val intent = Intent(this, activityDataUser::class.java)
        startActivity(intent)
    }
    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commit()
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

        if(providerSession == "Facebook") LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, WelcomeActivity::class.java))

    }
}