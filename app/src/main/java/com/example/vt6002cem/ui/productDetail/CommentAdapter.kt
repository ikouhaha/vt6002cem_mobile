package com.example.vt6002cem.ui.productDetail


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
import com.example.vt6002cem.databinding.FragmentProductCommentCardBinding
import com.example.vt6002cem.databinding.FragmentShoppingCartBinding
import com.example.vt6002cem.databinding.FragmentShoppingCartCardBinding
import com.example.vt6002cem.model.Comment
import com.example.vt6002cem.model.Product


class CommentAdapter (private val context: Context?) : RecyclerView.Adapter<CommentAdapter.MainViewHolder>() {


    var list = mutableListOf<Comment>()

    fun setCommentList(comments: List<Comment>) {
        this.list = comments.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentProductCommentCardBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val comment = list[position]

        holder.binding.comment = comment

        context?.let {
            Glide.with(it)
                .load(comment.avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                .into(holder.binding.avatarImg)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MainViewHolder(val binding: FragmentProductCommentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            itemView.setOnClickListener {
//                onItemClick?.invoke(list[adapterPosition])
//            }
//            binding.clearItem.setOnClickListener{
//                onDeleteShoppingCartClick?.invoke(list[adapterPosition])
//            }
        }
    }


}

