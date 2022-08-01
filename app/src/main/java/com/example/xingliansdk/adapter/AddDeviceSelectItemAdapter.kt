package com.example.xingliansdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xingliansdk.R
import com.example.xingliansdk.network.api.device.DeviceCategoryBean

class AddDeviceSelectItemAdapter(var list: MutableList<DeviceCategoryBean.DeviceCategoryItemBean.ProductListDTO>,val context: Context) : RecyclerView.Adapter<AddDeviceSelectItemAdapter.AddSelectItemViewHolder>(){


    class AddSelectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var itemNameTv : TextView ?= null

        init {
            itemNameTv = itemView.findViewById(R.id.itemAddSelectItemTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddSelectItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_tv_name_layout,parent,false)

        return AddSelectItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddSelectItemViewHolder, position: Int) {
       holder.itemNameTv?.text = list.get(position).productName
    }

    override fun getItemCount(): Int {
      return list.size
    }
}