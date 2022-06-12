package com.example.vt6002cem.ui.productDetail


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.common.Helper.toDateString
import com.example.vt6002cem.common.Helper.toString
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.databinding.FragmentProductDetailBinding
import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.model.Comment
import com.example.vt6002cem.model.User
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.repositroy.UserRepository
import com.example.vt6002cem.ui.login.LoginActivity
import com.example.vt6002cem.ui.shoppingCart.ShoppingCartAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var navigation: BottomNavigationView
    private var commentAdapter: CommentAdapter? = null
    private var id: Int? = null
    private var action: String? = null
    private lateinit var viewModel: ProductDetailViewModel
    private val database = FirebaseDatabase.getInstance(Config.firebaseRDBUrl)
    private var TAG = "ProductDetailFragment"
    private var commentRef: DatabaseReference? = null
    private var notificationRef: DatabaseReference? = null
    private var profile: User? = null
    private var _commentListener: ValueEventListener = object : ValueEventListener {
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

    fun cancelFocus(v: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
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
        if(Helper.getStoreString(context,"profile").isNullOrEmpty()){

        }else{
            profile = Gson().fromJson(Helper.getStoreString(context,"profile"),User::class.java)
        }

        commentAdapter = CommentAdapter(context)

    }

    fun validatePermisson(){
        if (auth.currentUser == null) {
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
        navigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        return root
    }

    override fun onStart() {
        super.onStart()
        commentRef = database.getReference("/comment/${id}")
        notificationRef = database.getReference("/notifications")
        if (Firebase.auth.currentUser == null) {
            init(null)
        } else {
            Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
                init(it.result.token)
            }
        }
    }

    private fun init(token: String?) {
        val retrofitService = ProductsApiService.getInstance(token)
        //val userRetrofitService = UserApiService.getInstance(token)
        val repository = Factory(
            ProductRepository(retrofitService)
        )

        binding.commentList.adapter = commentAdapter
        viewModel = ViewModelProvider(this, repository)[ProductDetailViewModel::class.java]
        initObserve()
        viewModel.getProduct(id!!)
        binding.let {
            Glide.with(this)
                .load(Config.imageUrl + id)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                .into(it.productImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        commentRef?.removeEventListener(_commentListener)
    }

    private fun initObserve() {
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
        viewModel.comment.observe(this) {
            binding.comment = it
        }
        viewModel.commentList.observe(this) {
            commentAdapter?.setCommentList(it)
        }
        viewModel.product.observe(this) {
            binding.product = it
        }

        binding.etComment.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                validatePermisson()
                Firebase.auth.currentUser?.let { user ->
                    viewModel.comment.value?.apply {
                        productId = id
                        commentBy = profile?.displayName
                        commentById = user.uid
                        commentDate = Helper.getCurrentDateTime().toDateString()
                        avatar = user.photoUrl?.toString()
                    }
                    viewModel.comment.value?.let {comment->
                        if(!comment.comment.isNullOrEmpty()){
                            commentRef?.push()?.setValue(comment)

                            //send notification send to company staff
                            if(profile?.role=="user"){
                              var newEntry =  notificationRef?.child("/${viewModel.product.value?.companyCode}")?.push()
                                comment.key = newEntry?.key
                                newEntry?.setValue(comment)
                            }else{
                                //staff send notification send to other user
                                val userIds = ArrayList<String?>()
                                commentRef?.get()?.addOnCompleteListener {snapshot->

                                    //check the user in the list
                                    //1.this comment

                                        if(comment.commentById!=user.uid){ //exclude self
                                            if(!userIds.contains(comment.commentById)){
                                                userIds.add(comment.commentById)
                                            }

                                    }
                                    //2. database comment
                                    for (sc in snapshot.result.children) {
                                        Log.d(TAG, "Value is: $sc")
                                        var sccomment: Comment? = sc.getValue(Comment::class.java)
                                        sccomment?.let {scc->
                                            if(scc.commentById!=user.uid){ //exclude self
                                                if(!userIds.contains(scc.commentById)){
                                                    userIds.add(scc.commentById)
                                                }
                                            }
                                        }
                                    }

                                    for(id in userIds){
                                        var newEntry = notificationRef?.child("/${id}")?.push()
                                        comment.key = newEntry?.key
                                        newEntry?.setValue(comment)
                                    }
                                }

                            }
                        }
                    }
                    viewModel.comment.postValue(Comment())
                    cancelFocus(binding.etComment)
                    handled = true

                }



            }
            handled
        }
        binding.addToShoppingCartBtn.setOnClickListener{
            validatePermisson()
            Firebase.auth.currentUser?.let {user->

                database.getReference("/cart/${user.uid}/${id}").setValue(true)
            }
        }
        if(Firebase.auth.currentUser!=null){
            commentRef?.addValueEventListener(_commentListener)
        }

    }

    inner class Factory constructor(
        private val repository: ProductRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
                ProductDetailViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}


