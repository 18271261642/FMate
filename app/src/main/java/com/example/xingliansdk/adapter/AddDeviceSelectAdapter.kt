package com.example.xingliansdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.network.api.device.DeviceCategoryBean

/**
 * Created by Admin
 *Date 2022/8/1
 */
class AddDeviceSelectAdapter(var data: DeviceCategoryBean, var context: Context): RecyclerView.Adapter<AddDeviceSelectAdapter.AddSelectViewHolder>() {

    private var bleScanItemClick : OnBleScanItemClick ?=null

    public fun setOnBleScanClickListener(click : OnBleScanItemClick){
        this.bleScanItemClick = click;
    }


    class AddSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var typeNameTv : TextView ?= null
         var typeImgView : ImageView ?= null
         var itemAddSelectRyView : RecyclerView ?= null

        init {
            typeNameTv = itemView.findViewById(R.id.itemAddSelectTypeNameTv)
            typeImgView = itemView.findViewById(R.id.itemAddSelectTypeImg)
            itemAddSelectRyView = itemView.findViewById(R.id.itemAddSelectRyView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddSelectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_add_select_layout,parent,false)
        return AddSelectViewHolder(view)
    }

     override fun onBindViewHolder(holder: AddSelectViewHolder, position: Int) {
        holder.typeNameTv?.text = data.list[position].name

         holder.typeImgView?.let { Glide.with(context).load(data.list[position].image).into(it) }

         val linearLayoutManager  = LinearLayoutManager(context)
         linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
         holder.itemAddSelectRyView?.layoutManager = linearLayoutManager

         val itemAdapter = AddDeviceSelectItemAdapter(data.list[position].productList,context)
         holder.itemAddSelectRyView?.adapter = itemAdapter


         holder.itemView.setOnClickListener(){
             bleScanItemClick?.onItemClick(holder.layoutPosition)
         }

    }

    override fun getItemCount(): Int {
      return if(data.list == null ) 0 else data.list.size
    }


    public interface OnBleScanItemClick{
        fun onItemClick(position : Int)
    }


}
