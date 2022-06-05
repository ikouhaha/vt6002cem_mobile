package com.example.vt6002cem

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vt6002cem.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)
    private lateinit var ref:DatabaseReference
    private val TAG:String = "Main"
    private lateinit var navController: NavController
    private lateinit var notificationBadge: BadgeDrawable
    private lateinit var cartBadge: BadgeDrawable

    var _taskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            var count = 0
            for (sc in snapshot.children) {
                Log.d(TAG, "Value is: $sc")
                count++
            }
            cartBadge.number = count

        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(this@MainActivity,"Failed to read value."+error.toException(), Toast.LENGTH_SHORT).show()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_shopping_cart, R.id.navigation_notifications,R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //supportActionBar?.setDisplayHomeAsUpEnabled(true);

        Firebase.auth.currentUser?.let { user->
            ref = database.getReference("${user.uid}/cart")
            ref.addValueEventListener(_taskListener)
        }
        notificationBadge = navView.getOrCreateBadge(R.id.navigation_notifications)
        notificationBadge.isVisible = false

        cartBadge = navView.getOrCreateBadge(R.id.navigation_shopping_cart)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true);
            it.setDisplayUseLogoEnabled(true);
            it.setLogo(R.drawable.ic_baseline_comment_24);
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ref.removeEventListener(_taskListener)
    }

    fun signOut(view: View){
        Firebase.auth.currentUser?.let {
            ref.removeEventListener(_taskListener)
            Firebase.auth.signOut()

            navController.navigate(R.id.navigation_home)
        }
    }
}