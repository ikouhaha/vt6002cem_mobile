package com.example.vt6002cem.ui.shoppingCart


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.databinding.FragmentShoppingCartBinding
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ShoppingCartFragment : Fragment() {

    private lateinit var binding: FragmentShoppingCartBinding
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)
    private lateinit var auth: FirebaseAuth
    private lateinit var navigation: BottomNavigationView
    private var id: Int? = null
    private var action: String? = null
    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var adapter: ShoppingCartAdapter
    private var ref: DatabaseReference? = null
    private var TAG = "ShoppingCart"

    var _taskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val arr = ArrayList<Int>()
            for (sc in snapshot.children) {
                Log.d(TAG, "Value is: $sc")
                sc.key?.let {key->
                    arr.add(key.toInt())
                }
            }
            viewModel.apply {
                ids.postValue(arr.toTypedArray())
                getProducts()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(
                activity,
                "Failed to read value." + error.toException(),
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    fun loading() {
        binding.indicator.show()

//        activity?.getWindow()?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun done() {
        binding.indicator.hide()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        var user = auth.currentUser
        if (user == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        adapter = ShoppingCartAdapter(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let { id = it.getInt("id") }
        arguments?.let { action = it.getString("action") }

        binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        navigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        return root
    }

    override fun onStart() {
        super.onStart()

        Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
            val retrofitService = ProductsApiService.getInstance(it.result.token)
            val repository = Factory(ProductRepository(retrofitService))
            binding.cartList.adapter = adapter
            viewModel = ViewModelProvider(this, repository).get(ShoppingCartViewModel::class.java)
            initObserve()

            ref = database.getReference("${Firebase.auth.currentUser!!.uid}/cart")
            ref?.addValueEventListener(_taskListener)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        ref?.removeEventListener(_taskListener)

    }

    fun initObserve() {

        viewModel.productList.observe(this) {
            adapter.setProductList(it)
            viewModel.ids.value?.let {array->
                for(id in array){
                    val find = it.find { product->product.id==id}
                    if(find==null){
                        database.getReference("${Firebase.auth.currentUser!!.uid}/cart/${id}").removeValue()
                    }
                }
            }

        }
        viewModel.errorMessage.observe(this) {msg->
            if(!msg.isNullOrEmpty()){
                Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                viewModel.errorMessage.postValue(null)
            }
        }
        viewModel.loading.observe(this) {
            if (it) {
                loading()
            } else {
                done()
            }
        }


        adapter.onDeleteShoppingCartClick = { product ->

            ref?.let { r ->
                r.get().addOnCompleteListener { g ->
                    for (sc in g.result.children) {
                        sc.key?.let { key ->
                            if (key.toInt() == product.id) {
                                sc.ref.removeValue()
                            }
                        }
                    }

                }
            }
        }
    }

    inner class Factory constructor(private val repository: ProductRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)) {
                ShoppingCartViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}


