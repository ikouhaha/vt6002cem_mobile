package com.example.vt6002cem.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.adpater.ProductsApiService
import com.example.vt6002cem.databinding.FragmentHomeBinding
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.ui.register.RegisterActivity
import com.example.vt6002cem.ui.register.RegisterRepository


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private  lateinit var viewModel:HomeViewModel
    private  var adapter = HomeProductAdapter()

    fun loading(){
        binding.indicator.show()
        activity?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun done(){
        binding.indicator.hide()
        activity?.getWindow()?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        );
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()

        val retrofitService = ProductsApiService.getInstance()
        val repository = Factory(HomeRepository(retrofitService))
        binding.recyclerview.adapter = adapter
        viewModel = ViewModelProvider(this,repository).get(HomeViewModel::class.java)
        initObserve()

        viewModel.getProducts()

    }

    fun initObserve(){
        // add observer
        viewModel.productList.observe(this) {
            adapter.setProductList(it)
        }
        viewModel.errorMessage.observe(this){
            Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
        }
        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                loading()
            }else{
                done()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private class Factory constructor(private val repository: HomeRepository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                HomeViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}