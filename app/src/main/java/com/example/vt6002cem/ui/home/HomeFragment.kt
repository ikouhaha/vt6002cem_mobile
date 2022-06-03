package com.example.vt6002cem.ui.home

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vt6002cem.databinding.FragmentHomeBinding
import com.example.vt6002cem.model.Product
import kotlinx.serialization.descriptors.PrimitiveKind


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.onCreated()
        homeViewModel.errorMessage.observe(viewLifecycleOwner){
            Toast.makeText(activity,it,Toast.LENGTH_LONG).show()
        }
        var p = Product()
        p.name = "test"
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root
        _binding?.indicator?.show()
        
        val gridView: GridView = _binding!!.gvShow
        val baseAdapter = activity?.let { Adapter(it, homeViewModel) }
        gridView.adapter = baseAdapter
        //Helper.loading(context)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}