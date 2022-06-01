package com.example.vt6002cem.ui.register

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.vt6002cem.Config
import com.example.vt6002cem.MainActivity
import com.example.vt6002cem.adpater.AuthApiService
import com.example.vt6002cem.adpater.UserApiService
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.ActivityRegisterBinding
import com.example.vt6002cem.model.User
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class RegisterActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    private var TAG = "Signup"
    enum class REQUEST(val value: Int){
        EMAIL(1),
        GOOGLE(2)
    }
    private val api = Retrofit.Builder()
        .baseUrl(Config.apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(Config.httpClient)
        .build()
        .create(UserApiService::class.java)



    fun loading(){
        binding.indicator.show()
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun done(){
        binding.indicator.hide()
        getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            );
    }

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

        //updateUI(currentUser);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==REQUEST.GOOGLE.value){
            val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = googleCredential.googleIdToken

            loading()
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
                                //val user = auth.currentUser

                                lifecycleScope.launch {


                                        var firebaseUser = auth.currentUser!!
                                        var user = User()
                                        user.email = firebaseUser.email
                                        user.password = (111111111..9999999999).random().toString()
                                        user.displayName = firebaseUser.displayName
                                        user.avatarUrl = firebaseUser.photoUrl.toString()
                                        user.fid = firebaseUser.uid
                                        user.role = "user" //default
                                        api.createUser(user).let { response ->
                                            if (response.isSuccessful) {

                                                startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                                            } else {
                                                Toast.makeText(getBaseContext(),"onFailure: ${response.message()}",Toast.LENGTH_LONG).show()
                                            }
                                        }
                                        done()
                                    }
                            } else {
                                // If sign in fails, display a message to the user.
                                binding.indicator.hide()
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                                //updateUI(null)
                                done()

                            }
                        }
                }
                else -> {
                    // Shouldn't happen.
                    Log.d(TAG, "No ID token!")
                    binding.indicator.hide()
                    done()
                }
            }
        }



    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel



    }

    fun googleSignin(view: View){

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(
                this
            ) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQUEST.GOOGLE.value, null, 0, 0, 0
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