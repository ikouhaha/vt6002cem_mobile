package com.example.vt6002cem.ui.register

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.ActivityLoginBinding
import com.example.vt6002cem.databinding.ActivityRegisterBinding
import com.example.vt6002cem.model.User
import com.example.vt6002cem.ui.login.LoginActivity
import com.example.vt6002cem.ui.login.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private var TAG = "Signup"

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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        viewModel.user.apply {
            value = User()
        }
        binding.viewModel = viewModel
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
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: " + e.localizedMessage)
                }
            }
            .addOnFailureListener(this) { e -> // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
    }


    fun signup(view: View) {
        if(viewModel.isFormValid()){

        }
    }


    fun goToLogin(view: View) {
        val intent= Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}