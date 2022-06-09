package com.example.vt6002cem.ui.settings


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.vt6002cem.R
import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.databinding.FragmentSettingsBinding
import com.example.vt6002cem.repositroy.UserRepository
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private var _token = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var navigation:BottomNavigationView
    private  var viewModel: SettingsViewModel? = null
    private val TAG = "SettingsFragment"
    // This property is only valid between onCreateView and
    // onDestroyView.
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel = null
    }

    fun loading(){
        binding?.indicator?.show()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun done(){
        binding?.indicator?.hide()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        var user  = auth.currentUser
        if (user == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        return root
    }


    override fun onStart() {
        super.onStart()

        if(Firebase.auth.currentUser==null){
            init(null)
        }else{
            Firebase.auth.currentUser?.getIdToken(false)?.addOnCompleteListener { it ->

                Log.d(TAG,it.result.token.toString())
                init(it.result.token)
            }
        }
    }

    fun init(token:String?){
        val retrofitService = UserApiService.getInstance(token)
        val repository = Factory(UserRepository(retrofitService))
        viewModel = ViewModelProvider(this,repository)[SettingsViewModel::class.java]
        binding!!.viewModel = viewModel
        initObserve()
        viewModel?.getProfile()

    }
    fun initObserve() {
        viewModel?.let {
            it.errorMessage.observe(this){msg->
                if(!msg.isNullOrEmpty()){
                    Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                    it.errorMessage.postValue(null)
                }
            }
            it.isSave.observe(this){isSave->
                if(isSave){
                    Toast.makeText(activity,"Save Successfully",Toast.LENGTH_SHORT).show()
                    it.isSave.postValue(false)
                }
            }
            it.loading.observe(this){
                if(it){
                    loading()
                }else{
                    done()
                }
            }
            it.user.observe(this){
                binding!!.viewModel = viewModel
                Glide.with(this)
                    .load(it.avatarUrl)
                    .placeholder(R.mipmap.ic_image_placeholder_foreground)
                    .into(binding!!.avatarImg)
                if(it.role=="user"){
                    binding!!.radioGroup.check(R.id.user)
                }else if(it.role=="staff"){
                    binding!!.radioGroup.check(R.id.staff)
                }
            }
        }

    }


    inner class Factory constructor(private val repository: UserRepository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                SettingsViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}


