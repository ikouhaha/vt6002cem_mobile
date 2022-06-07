package com.example.vt6002cem.ui.productDetail


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.databinding.FragmentProductDetailBinding
import com.example.vt6002cem.repositroy.ProductRepository
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
        if(Firebase.auth.currentUser==null){
            init(null)
        }else{
            Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
                init(it.result.token)
            }
        }
    }

    fun init(token:String?){
        val retrofitService = ProductsApiService.getInstance(token)
        val repository = Factory(ProductRepository(retrofitService))

        viewModel = ViewModelProvider(this,repository).get(ProductDetailViewModel::class.java)
        initObserve()
        viewModel.getProduct(id!!)
        binding.let {

            Glide.with(this)
                .load(Config.imageUrl+id)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                .into(it.productImage)
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

    inner class Factory constructor(private val repository: ProductRepository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
                ProductDetailViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}


