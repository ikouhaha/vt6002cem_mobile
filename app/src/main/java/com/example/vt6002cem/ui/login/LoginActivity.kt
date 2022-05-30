package com.example.vt6002cem.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.ActivityLoginBinding
import com.example.vt6002cem.databinding.ActivityMainBinding
import com.example.vt6002cem.model.User
import com.example.vt6002cem.ui.home.HomeViewModel
import com.example.vt6002cem.ui.register.RegisterActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var TAG = "Login"



    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(Helper.getStringFromName(this,"default_web_client_id"))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build();

        var currentUser = auth.getCurrentUser()

        //updateUI(currentUser);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.user.apply {
            value = User()
        }
        binding.viewModel = viewModel
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = googleCredential.googleIdToken

        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = auth.currentUser

                            GlobalScope.launch {
                                Log.d(TAG,Helper.getToken());
                                Log.d(TAG, "signInWithCredential:token success")
                            }


                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            //updateUI(null)
                        }
                    }
            }
            else -> {
                // Shouldn't happen.
                Log.d(TAG, "No ID token!")
            }
        }
    }

    fun googleSignin(view: View){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(
                this
            ) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 2, null, 0, 0, 0
                    )
                } catch (e: SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: " + e.localizedMessage)
                }
            }
            .addOnFailureListener(this) { e -> // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
    }

    fun goToRegister(view: View) {
        val intent= Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        if(viewModel.isFormValid()){

        }
    }


}