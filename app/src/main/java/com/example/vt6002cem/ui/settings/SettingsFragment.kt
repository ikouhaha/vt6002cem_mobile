package com.example.vt6002cem.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.databinding.FragmentSettingsBinding
import com.example.vt6002cem.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private var _token = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var navigation:BottomNavigationView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


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


        _token = Helper.getStoreString(this.context, "token")

        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        navigation =requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        _binding?.textButton?.setOnClickListener {
            auth?.signOut()
            navigation?.selectedItemId = R.id.navigation_home

        };

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


