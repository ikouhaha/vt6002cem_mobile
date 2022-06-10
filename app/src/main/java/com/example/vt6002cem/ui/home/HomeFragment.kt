package com.example.vt6002cem.ui.home

import android.Manifest
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.location.*
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

    protected var mLatitudeText: String = ""
    protected var mLongitudeText: String = ""
    protected var mTimeText: String = ""
    protected var mOutput: String = ""

    // member variables that hold location info
    protected var mLastLocation: Location? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mGeocoder: Geocoder? = null
    protected var mLocationProvider: FusedLocationProviderClient? = null
    protected  var thoroughfare:String = ""

    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {

            mLastLocation = result.lastLocation
            mLatitudeText = mLastLocation!!.latitude.toString()
            mLongitudeText = mLastLocation!!.longitude.toString()
            mTimeText = DateFormat.getTimeInstance().format(Date())

            mGeocoder = Geocoder(requireActivity())
            try {
                // Only 1 address is needed here.
                val addresses = mGeocoder!!.getFromLocation(
                    mLastLocation!!.latitude, mLastLocation!!.longitude, 1
                )
                if (addresses.size == 1) {
                    val address = addresses[0]
                    val addressLines = StringBuilder()
                    if (address.maxAddressLineIndex > 0) {
                        for (i in 0 until address.maxAddressLineIndex) {
                            addressLines.append(
                                """
${address.getAddressLine(i)}

""".trimIndent()
                            )
                        }
                    } else {
                        addressLines.append(address.getAddressLine(0))
                    }
                    thoroughfare = address.thoroughfare
                    mOutput = addressLines.toString()
                    showGpsDialog(thoroughfare)

                } else {
                    mOutput = "WARNING! Geocoder returned more than 1 addresses!"
                }
            } catch (e: Exception) {
            }
        }


    }

    private fun showGpsDialog(area:String) {
        context?.let {
            AlertDialog.Builder(it) // set message, title, and icon
                .setTitle("Find")
                .setMessage("Do you want to find nearby ${area}")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
                        viewModel?.clearList()
                        viewModel?.getProducts()
                        dialog.dismiss()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()?.show()
            mLocationProvider?.removeLocationUpdates(mLocationCallBack)
            binding?.ivGpsSearch.setIconResource(R.drawable.ic_baseline_gps_not_fixed_24)
        }
    }

    private fun AskDeleteOption(product:Product): AlertDialog? {
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



    fun onLocateStartClicked() {
        binding?.ivGpsSearch.setIconResource(R.drawable.ic_baseline_gps_fixed_24)
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1
        mLocationRequest!!.fastestInterval = 1
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        mTimeText = "Started updating location"
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLocationProvider!!.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallBack, Looper.getMainLooper()
        )

    }

    fun onLocateClicked(view: View) {
        // LocationReques sets how often etc the app receives location updates

        mGeocoder = Geocoder(requireActivity())
        try {
            // Only 1 address is needed here.
            val addresses = mGeocoder!!.getFromLocation(
                mLastLocation!!.latitude, mLastLocation!!.longitude, 1
            )
            if (addresses.size == 1) {
                val address = addresses[0]
                val addressLines = StringBuilder()
                //see herehttps://stackoverflow
                // .com/questions/44983507/android-getmaxaddresslineindex-returns-0-for-line-1
                if (address.maxAddressLineIndex > 0) {
                    for (i in 0 until address.maxAddressLineIndex) {
                        addressLines.append(
                            """
${address.getAddressLine(i)}
""".trimIndent()
                        )
                    }
                } else {
                    addressLines.append(address.getAddressLine(0))
                }
                mOutput = addressLines.toString()
            } else {
                mOutput = "WARNING! Geocoder returned more than 1 addresses!"
            }
        } catch (e: Exception) {
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
        binding?.ivGpsSearch.setIconResource(R.drawable.ic_baseline_gps_not_fixed_24)
        navController = findNavController(this)


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
        binding!!.ivGpsSearch.setOnClickListener{
            onLocateStartClicked()
        }
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
            val diaBox = AskDeleteOption(product)
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