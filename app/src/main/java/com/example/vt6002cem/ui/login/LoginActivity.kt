package com.example.vt6002cem.ui.login

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.MainActivity
import com.example.vt6002cem.common.CryptographyUtil
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.ActivityLoginBinding
import com.example.vt6002cem.model.EncryptedMessage
import com.example.vt6002cem.model.User
import com.example.vt6002cem.ui.register.RegisterActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executor
import javax.crypto.Cipher


class LoginActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var executor: Executor
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var cryptoObject:BiometricPrompt.CryptoObject
    private var canFingerPrint = false
    private val TAG = "Login"
    private var KEY_NAME = "Login"
    private var cipher: Cipher? = null
    private var encryptedMessage: EncryptedMessage? = null
    private var encryptedString:String?= null


    private enum class ACTION {
        LOGIN, FINGERPRINT
    }

    private var action = ACTION.LOGIN

    enum class REQUEST(val value: Int) {
        EMAIL(1),
        GOOGLE(2)
    }


    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        var user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(Helper.getStringFromName(this, "default_web_client_id"))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build();
        //updateUI(currentUser);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cipher = CryptographyUtil.getInitializedCipherForEncryption()
        encryptedMessage = Helper.getEncryptedMessage(this, KEY_NAME)
        //val encryptedMessage = CryptographyUtil.encryptData("test", cipher!!)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.user.apply {
            value = User()
        }
        binding.viewModel = viewModel
        canFingerPrint = Helper.getStoreBoolean(this, "canFingerPrint")
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)



                    if(action==ACTION.LOGIN){
                        encryptedMessage?.cipherText?.let { it ->
                            result.cryptoObject?.cipher?.let { cipher ->
                                val decryptedMessage = CryptographyUtil.decryptData(it, cipher)
                                val email = decryptedMessage.split(":")[0]
                                val password = decryptedMessage.split(":")[1]
                                emailAuth(email, password)
                            }
                        }
                    }else if(action==ACTION.FINGERPRINT){
                        cipher?.let { cipher ->
                            val encryptedMessage = CryptographyUtil.encryptData(encryptedString!!, cipher)
                            Helper.storeEncryptedMessage(
                                this@LoginActivity,
                                KEY_NAME,
                                encryptedMessage = encryptedMessage
                            )
                        }
                        goToHome()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })


        if (canFingerPrint && encryptedMessage != null) {
            encryptedMessage?.initializationVector?.let { it ->
                // Retrieve Cryptography Object
                cryptoObject = BiometricPrompt.CryptoObject(
                    CryptographyUtil.getInitializedCipherForDecryption(it)
                )
                // Show BiometricPrompt With Cryptography Object
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for qipao shop")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build()
                biometricPrompt.apply {
                    if (cryptoObject == null) authenticate(promptInfo)
                    else authenticate(promptInfo, cryptoObject)
                }

            }

        }
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
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
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

    fun googleSignin(view: View) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(
                this
            ) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQUEST.GOOGLE.value, null, 0, 0, 0
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
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

    }
    fun goToHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun emailAuth(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if(encryptedMessage==null){
                    //ask confirm register finger print
                    promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login for my app")
                        .setSubtitle("Register finger print to futher login?")
                        .setNegativeButtonText("Use account password")
                        .build()
                    action = ACTION.FINGERPRINT
                    encryptedString = "$email:$password"
                    biometricPrompt.apply {
                         authenticate(promptInfo)
                    }
                }else{
                    goToHome()
                }

            }
        }.addOnFailureListener { exception ->
            Helper.clearEncryptedMessage(this,KEY_NAME)
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()

        }
    }

    fun login(view: View) {
        if (viewModel.isFormValid()) {
            viewModel.user.value?.let { user ->
                emailAuth(user.email!!, user.password!!)
            }
        }
    }
}