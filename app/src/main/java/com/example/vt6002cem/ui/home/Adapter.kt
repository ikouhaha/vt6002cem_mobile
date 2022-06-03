package com.example.vt6002cem.ui.home

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.vt6002cem.R
import com.example.vt6002cem.BR




class Adapter(context: Context,viewModel:HomeViewModel): BaseAdapter() {

    private var dataBinding: ViewDataBinding? = null
    private val mContext: Context = context

    val list = viewModel.productList

    override fun getCount(): Int {
        return list.value!!.size
    }

    override fun getItem(index: Int): Any {
        return list.value!![index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getView(index: Int, view: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        if (view == null) {
            dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home_card, parent, false);
        }else{
            dataBinding = DataBindingUtil.getBinding(view)
        }

        dataBinding!!.setVariable(BR.product,getItem(index))

        return dataBinding!!.getRoot();
    }
}