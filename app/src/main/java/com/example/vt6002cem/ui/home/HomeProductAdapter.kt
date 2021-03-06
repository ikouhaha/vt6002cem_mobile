package com.example.vt6002cem.ui.home


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.databinding.FragmentHomeCardBinding
import com.example.vt6002cem.model.Product


class HomeProductAdapter (private val context: Context?) : RecyclerView.Adapter<HomeProductAdapter.MainViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null
    var onShoppingCartClick: ((Product) -> Unit)? = null
    var onEditButtonClick: ((Product) -> Unit)? = null
    var onDeleteButtonClick: ((Product) -> Unit)? = null
    var list = mutableListOf<Product>()

    fun setProductList(products: List<Product>) {
        this.list = products.toMutableList()
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
        holder.binding.position = position


        val url: String = Config.imageUrl+product.id

        context?.let {
            Glide.with(it)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                .into(holder.binding.productImage)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MainViewHolder(val binding: FragmentHomeCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(list[adapterPosition])
            }
            binding.addToShoppingCartBtn.setOnClickListener{
                onShoppingCartClick?.invoke(list[adapterPosition])
            }
            binding.editButton.setOnClickListener{
                onEditButtonClick?.invoke(list[adapterPosition])
            }
            binding.deleteButton.setOnClickListener{
                onDeleteButtonClick?.invoke(list[adapterPosition])
            }
        }
    }


}

