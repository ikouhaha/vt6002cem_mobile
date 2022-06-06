package com.example.vt6002cem.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.databinding.FragmentHomeBinding
import com.example.vt6002cem.repositroy.ProductRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private  var viewModel:HomeViewModel? = null
    private  lateinit  var adapter:HomeProductAdapter
    private  lateinit var  navController: NavController
    private var TAG = "Home"
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)


    fun loading(){
        binding.indicator.show()

//        activity?.getWindow()?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun done(){
        binding.indicator.hide()
        if(binding.swipeRefreshLayout.isRefreshing){
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = HomeProductAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        navController = findNavController(this)
        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
        if(Firebase.auth.currentUser==null){
            init(null)
        }else{
            Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
                init(it.result.token)
            }
        }
    }

    fun init(token:String?){
        val retrofitService = ProductsApiService.getInstance(token)
        val repository = Factory(ProductRepository(retrofitService))
        binding.recyclerview.adapter = adapter
        viewModel = ViewModelProvider(this,repository)[HomeViewModel::class.java]
        initObserve()
        viewModel?.getProducts()
    }

    fun cancelFocus(v:View){
        val imm =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
    fun initObserve(){
        // add observer
        viewModel?.let {
            it.productList.observe(this) {
                adapter.setProductList(it)
            }
            it.errorMessage.observe(this){
                Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
            }
            it.loading.observe(this){
                if(it){
                    loading()
                }else{
                    done()
                }
            }
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.text = null
        }
        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ivClear.visibility = View.VISIBLE
            } else {
                binding.ivClear.visibility = View.GONE
                cancelFocus(binding.etSearch)
            }
        }
        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel?.search(v.editableText)
                cancelFocus(binding.etSearch)
                handled = true
            }
            handled
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel?.refreshList()
        }
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel?.let {
                    if (it.loading.value!=true&&!recyclerView.canScrollVertically(1)) { //1 for down
                        it.loadMore()
                    }
                }

            }
        })
        adapter.onItemClick = {

            // do something with your item
            val bundle = Bundle()
            bundle.putInt("id", it.id!!)
            bundle.putString("action", "view")
            navController.navigate(R.id.navigation_product_detail,bundle)

        }

        adapter.onShoppingCartClick = { product ->
            Log.d(TAG, product.id.toString())
            Firebase.auth.currentUser?.let {user->

                database.getReference("${user.uid}/cart/${product.id}").setValue(true)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel?.clearList()
    }

    fun viewDetail(view:View){

    }

    inner class Factory constructor(private val repository: ProductRepository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                HomeViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}