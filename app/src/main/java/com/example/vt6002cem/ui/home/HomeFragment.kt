package com.example.vt6002cem.ui.home

import android.Manifest
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.databinding.FragmentHomeBinding
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private  var viewModel:HomeViewModel? = null
    private  lateinit  var adapter:HomeProductAdapter
    private  lateinit var  navController: NavController
    private var TAG = "Home"
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)

    protected var mLatitudeText: TextView? = null
    protected var mLongitudeText: TextView? = null
    protected var mTimeText: TextView? = null
    protected var mOutput: TextView? = null
    protected var mLocateButton: Button? = null

    // member variables that hold location info
    protected var mLastLocation: Location? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mGeocoder: Geocoder? = null
    protected var mLocationProvider: FusedLocationProviderClient? = null

    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            mLastLocation = result.lastLocation
            mLatitudeText!!.text = mLastLocation!!.latitude.toString()
            mLongitudeText!!.text = mLastLocation!!.longitude.toString()
            mTimeText!!.text = DateFormat.getTimeInstance().format(Date())
        }
    }

    private fun AskOption(product:Product): AlertDialog? {
        return context?.let {
            AlertDialog.Builder(it) // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
                        viewModel?.deletePost(product.id!!,product.companyCode!!)
                        dialog.dismiss()
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()
        }
    }

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

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String?, Boolean?>> { result: Map<String?, Boolean?> ->
                val fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                )
                val coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                )
                if (fineLocationGranted != null && fineLocationGranted) {
                    // Precise location access granted.
                    // permissionOk = true;
                    mTimeText!!.text = "permission granted"
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Only approximate location access granted.
                    // permissionOk = true;
                    mTimeText!!.text = "permission granted"
                } else {
                    // permissionOk = false;
                    // No location access granted.
                    mTimeText!!.text = "permission not granted"
                }
            }
        )

        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
        viewModel?.clearList()
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
            it.productList.observe(this) {list->
                adapter.setProductList(list)
            }
            it.errorMessage.observe(this){msg->
                if(!msg.isNullOrEmpty()){
                    Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                    it.errorMessage.postValue(null)
                }

            }
            it.loading.observe(this){loading->
                if(loading){
                    loading()
                }else{
                    done()
                }
            }
//            it.isDelete.observe(this){isDelete->
//                if(isDelete){
//                    viewModel?.clearList()
//                    viewModel?.getProducts()
//                    it.isDelete.postValue(false)
//                }
//            }
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
            val bundle = Bundle()
            bundle.putInt("id", it.id!!)
            navController.navigate(R.id.action_navigation_home_to_navigation_product_detail,bundle)

        }

        adapter.onShoppingCartClick = { product ->
            Log.d(TAG, product.id.toString())
            if(Firebase.auth.currentUser==null){
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            Firebase.auth.currentUser?.let {user->
                database.getReference("/cart/${user.uid}/${product.id}").setValue(true)
            }
        }
        adapter.onEditButtonClick = { product ->
            val bundle = Bundle()
            Log.d(TAG, product.id.toString())
            bundle.putInt("id", product.id!!)
            navController.navigate(R.id.action_navigation_home_to_navigation_edit_post,bundle)

        }
        adapter.onDeleteButtonClick = { product ->
            Log.d(TAG, product.id.toString())
            val diaBox = AskOption(product)
            diaBox?.show()

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