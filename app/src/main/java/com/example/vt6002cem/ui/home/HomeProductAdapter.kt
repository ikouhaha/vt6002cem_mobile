package com.example.vt6002cem.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vt6002cem.databinding.FragmentHomeCardBinding
import com.example.vt6002cem.model.Product

class HomeProductAdapter : RecyclerView.Adapter<MainViewHolder>() {

    var list = mutableListOf<Product>()

    fun setProductList(products: List<Product>) {
        this.list = products.toMutableList()
        notifyDataSetChanged()
    }

    fun clearList(){
        list.clear()
    }

    fun addProductList(products: List<Product>) {
        if(this.list ==null){
            this.list = products.toMutableList()
        }else{
            this.list.addAll(products)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentHomeCardBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val product = list[position]
        holder.binding.product = product
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class MainViewHolder(val binding: FragmentHomeCardBinding) : RecyclerView.ViewHolder(binding.root) {

}