package com.example.vt6002cem


import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.ActivityMainBinding
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.model.User
import com.example.vt6002cem.repositroy.UserRepository
import com.example.vt6002cem.ui.register.RegisterActivity
import com.google.android.gms.location.LocationRequest
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)
    private var ref: DatabaseReference? = null
    private var notificationRef: DatabaseReference? = null
    private val TAG: String = "Main"
    private lateinit var navController: NavController
    private lateinit var notificationBadge: BadgeDrawable
    private lateinit var cartBadge: BadgeDrawable
    private var profile:User? = null
    private var navView: BottomNavigationView? = null

    var _taskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            var count = 0
            for (sc in snapshot.children) {
                Log.d(TAG, "Value is: $sc")
                count++
            }
            if (count > 0) {
                cartBadge.isVisible = true
                cartBadge.number = count
            } else {
                cartBadge.isVisible = false
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(
                this@MainActivity,
                "Failed to read value." + error.toException(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    var _notifyListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            var count = 0
            for (sc in snapshot.children) {
                Log.d(TAG, "Value is: $sc")
                count++
            }
            if (count > 0) {
                notificationBadge.isVisible = true
                notificationBadge.number = count
            } else {
                notificationBadge.isVisible = false
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(
                this@MainActivity,
                "Failed to read value." + error.toException(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gpsInit()
        if (Firebase.auth.currentUser == null) {
            init()
            navView?.menu?.findItem(R.id.navigation_create_post)?.isVisible = false

        } else {
            var idRequest = Firebase.auth.currentUser?.getIdToken(true)
            idRequest?.addOnCompleteListener { it ->
                val repository = UserRepository(UserApiService.getInstance(it.result.token))
                lifecycleScope.launch {
                    val response = repository.getProfile()
                    if (response.isSuccessful) {
                        response.body()?.let {u->
                            if(u.role=="staff"){
                                notificationRef = database.getReference("/notifications/${u.companyCode}")
                            }else{
                                notificationRef = database.getReference("/notifications/${Firebase.auth.currentUser!!.uid}")
                            }

                            ref = database.getReference("/cart/${Firebase.auth.currentUser!!.uid}")
                            ref?.addValueEventListener(_taskListener)
                            notificationRef?.addValueEventListener(_notifyListener)


                            Helper.setStoreString(this@MainActivity, "profile", Gson().toJson(u))

                            init()

                            navView?.menu?.findItem(R.id.navigation_create_post)?.isVisible = u.role=="staff"

                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error : ${response.message()} ",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            }
        }


    }

    fun gpsInit(){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String?, Boolean?>> { result: Map<String?, Boolean?> ->
                val fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                )
                val coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                )

            }
        )

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )



    }


    fun init() {

        setContentView(binding.root)

        navView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_shopping_cart,
                R.id.navigation_create_post,
                R.id.navigation_notifications,
                R.id.navigation_settings
            )

        )



        setupActionBarWithNavController(navController, appBarConfiguration)


        navView!!.setupWithNavController(navController)

        //supportActionBar?.setDisplayHomeAsUpEnabled(true);

        notificationBadge = navView!!.getOrCreateBadge(R.id.navigation_notifications)

        notificationBadge.isVisible = false

        cartBadge = navView!!.getOrCreateBadge(R.id.navigation_shopping_cart)
        cartBadge.isVisible = false


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
        ref?.removeEventListener(_taskListener)
        notificationRef?.removeEventListener(_notifyListener)
    }

    fun signOut(view: View) {
        Firebase.auth.currentUser?.let {
            ref?.removeEventListener(_taskListener)
            Firebase.auth.signOut()
            navController.navigate(R.id.navigation_home)
        }
    }

    //  life cycle function , pop up if available
    override fun onResume() {
        super.onResume()



        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Helper.setStoreBoolean(this, "canFingerPrint", true)
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
//                biometricStatusTextView.text = "No biometric features available on this device."
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
//                biometricStatusTextView.text = "Biometric features are currently unavailable."
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
//                // Prompts the user to create credentials that your app accepts.
//                biometricStatusTextView.text = "Biometric features are not enrolled."
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.bottom_nav_menu, menu);
//        return true;
//    }
}