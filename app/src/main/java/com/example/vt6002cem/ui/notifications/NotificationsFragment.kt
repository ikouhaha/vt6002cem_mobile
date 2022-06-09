package com.example.vt6002cem.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.common.Helper.toDateString
import com.example.vt6002cem.databinding.FragmentNotificationsBinding
import com.example.vt6002cem.model.Comment
import com.example.vt6002cem.model.User
import com.example.vt6002cem.ui.productDetail.CommentAdapter
import com.example.vt6002cem.ui.productDetail.ProductDetailViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private  lateinit var  navController: NavController
    private lateinit var viewModel: NotificationsViewModel
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)
    private var TAG = "ProductDetailFragment"
    private var ref: DatabaseReference? = null
    private var adapter: NotificationAdapter? = null
    private var profile: User? = null
    private var _notificationListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val arr = ArrayList<Comment>()

            for (sc in snapshot.children) {
                Log.d(TAG, "Value is: $sc")
                var comment: Comment? = sc.getValue(Comment::class.java)
                comment?.let {  arr.add(it) }

            }
            viewModel.commentList.postValue(arr)
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

        if(Helper.getStoreString(context,"profile").isNullOrEmpty()){

        }else{
            profile = Gson().fromJson(Helper.getStoreString(context,"profile"),User::class.java)
        }

        adapter = NotificationAdapter(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.commentList.adapter = adapter
        navController = NavHostFragment.findNavController(this)




        return binding.root
    }

    override fun onStart() {
        super.onStart()
        ref = database.getReference("/notifications")

        if(profile?.role=="staff"){
            ref?.child("/${profile?.companyCode}")?.get()?.addOnCompleteListener {snapshot->
                val  arr:ArrayList<Comment> = arrayListOf()
                for (sc in snapshot.result.children) {
                    val sccomment: Comment? = sc.getValue(Comment::class.java)
                    sccomment?.let {scc->
                        arr.add(sccomment)
                    }

                }
                viewModel.commentList.postValue(arr)
            }
        }else{
            ref?.child("/${profile?.fid}")?.get()?.addOnCompleteListener {snapshot->
                val  arr:ArrayList<Comment> = arrayListOf()
                for (sc in snapshot.result.children) {
                    val sccomment: Comment? = sc.getValue(Comment::class.java)
                    sccomment?.let {scc->
                        arr.add(sccomment)
                    }

                }
                viewModel.commentList.postValue(arr)
            }
        }
        initObserve()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initObserve() {
        adapter?.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", it.productId!!)
            bundle.putString("action", "view")
            if(profile?.role=="staff"){
                ref?.child("/${profile?.companyCode}/${it.key}")?.removeValue()
            }else{
                ref?.child("/${profile?.fid}/${it.key}")?.removeValue()
            }

            navController.navigate(R.id.navigation_product_detail,bundle)

        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.loading.observe(this) {
            if (it) {
                loading()
            } else {
                done()
            }
        }
        viewModel.commentList.observe(this) {
            adapter?.setCommentList(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}