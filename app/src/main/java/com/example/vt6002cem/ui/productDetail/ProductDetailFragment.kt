package com.example.vt6002cem.ui.productDetail


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.adpater.ProductsApiService
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.FragmentProductDetailBinding
import com.example.vt6002cem.databinding.FragmentSettingsBinding
import com.example.vt6002cem.ui.home.HomeRepository
import com.example.vt6002cem.ui.home.HomeViewModel
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var navigation:BottomNavigationView
    private  var id: Int? = null
    private  var action: String? = null
    private  lateinit var viewModel:ProductDetailViewModel

    private var TAG = "Home"
    fun loading(){
        binding.indicator.show()

//        activity?.getWindow()?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun done(){
        binding.indicator.hide()


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
        arguments?.let { id = it.getInt("id") }
        arguments?.let { action = it.getString("action") }

        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        navigation =requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        return root
    }

    override fun onStart() {
        super.onStart()

        Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
            val retrofitService = ProductsApiService.getInstance(it.result.token)
            val repository = Factory(ProductDetailRepository(retrofitService))

            viewModel = ViewModelProvider(this,repository).get(ProductDetailViewModel::class.java)
            initObserve()
            viewModel.getProduct(id!!)
            binding.let {

                Glide.with(this)
                    .load(Config.imageUrl+id)
                    .placeholder(R.mipmap.ic_image_placeholder_foreground)
                    .into(it.productImage)
            }
            //binding.viewModel = viewModel

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun initObserve(){
        viewModel.errorMessage.observe(this){
            Toast.makeText(activity,it, Toast.LENGTH_SHORT).show()
        }
        viewModel.loading.observe(this){
            if(it){
                loading()
            }else{
                done()
                binding.viewModel = viewModel
            }
        }
    }

    inner class Factory constructor(private val repository: ProductDetailRepository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
                ProductDetailViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}

