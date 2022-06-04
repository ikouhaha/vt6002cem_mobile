package com.example.vt6002cem.ui.productDetail


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    private var binding: FragmentProductDetailBinding? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var navigation:BottomNavigationView
    private  var id: Int? = null
    private  var action: String? = null
    private  lateinit var viewModel:ProductDetailViewModel

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

        val productDetailViewModel =
            ViewModelProvider(this).get(ProductDetailViewModel::class.java)

        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        navigation =requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        return root
    }

    override fun onStart() {
        super.onStart()

        Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
            val retrofitService = ProductsApiService.getInstance(it.result.token)
            val repository = Factory(ProductDetailRepository(retrofitService))

            viewModel = ViewModelProvider(this,repository).get(ProductDetailViewModel::class.java)
            initObserve()
            viewModel.getProduct(id!!)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun initObserve(){

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


