package com.example.vt6002cem.ui.createPost


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.common.Validations
import com.example.vt6002cem.databinding.FragmentCreatePostBinding
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.login.LoginActivity
import com.example.vt6002cem.ui.post.CreatePostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888
    private val UPDATE_IMAGE_ACTIVITY_REQUEST_CODE = 1999
    private lateinit var auth: FirebaseAuth
    private  lateinit var  navController: NavController
    private var id: Int? = null
    private var action: String? = null
    private lateinit var viewModel: CreatePostViewModel
    private var isInit: Boolean = false


    private var TAG = "CreatePost"
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
        navController = NavHostFragment.findNavController(this)
        var user = auth.currentUser
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

        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        return root
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser == null) {
            init(null)
        } else {
            Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
                init(it.result.token)
            }
        }
    }

    fun init(token: String?) {
        isInit = false
        val retrofitService = ProductsApiService.getInstance(token)
        val repository = Factory(ProductRepository(retrofitService))
        viewModel = ViewModelProvider(this, repository)[CreatePostViewModel::class.java]
        binding.viewModel = viewModel
        initObserve()


    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun initObserve() {
        viewModel?.let {
            it.errorMessage.observe(this) { msg ->
                if (!msg.isNullOrEmpty()) {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    it.errorMessage.postValue(null)
                }

            }
            it.loading.observe(this) {
                if (it) {
                    loading()
                } else {
                    done()
                    // binding.viewModel = viewModel
                }
            }
            it.product.observe(this) { product ->
                binding.product = product
            }
            it.actionTextToast.observe(this) { text ->
                if (!text.isNullOrEmpty()) {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.navigation_home)
                    viewModel.product.postValue(Product())
                    it.actionTextToast.postValue("")
//                        requireActivity().viewModelStore.clear();
                }
            }
        }



        binding.captureImageBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(
                intent,
                CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }
        binding.uploadImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                intent,
                UPDATE_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }
        binding.actionButton.setOnClickListener {

            if (viewModel.isFormValid()) {
                viewModel.createPost()
            } else {
                //set error
                binding.viewModel?.product?.value?.let {
                    binding.priceEdit.error = Validations.number(it.price)
                }

            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val bmp = data?.extras!!["data"] as Bitmap?
                val stream = ByteArrayOutputStream()
                bmp!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()

                // convert byte array to Bitmap
                val bitmap = BitmapFactory.decodeByteArray(
                    byteArray, 0,
                    byteArray.size
                )
                binding.productImage.setImageBitmap(bitmap)
                Helper.convertToBase64(bitmap)?.let { base64 ->
                    viewModel.product.value?.apply {
                        imageBase64 = base64
                    }
                }
            }
        } else if (requestCode == UPDATE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data ?: return
                var imageInputStream = requireActivity().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(imageInputStream)
                binding.productImage.setImageBitmap(bitmap)
                Helper.convertToBase64(bitmap)?.let { base64 ->
                    viewModel.product.value?.apply {
                        imageBase64 = base64
                    }
                }

            }
        }
    }


    inner class Factory constructor(private val repository: ProductRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(CreatePostViewModel::class.java)) {
                CreatePostViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}


