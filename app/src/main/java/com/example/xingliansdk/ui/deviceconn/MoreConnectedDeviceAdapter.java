package com.example.xingliansdk.ui.deviceconn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xingliansdk.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/7/27
 */
public class MoreConnectedDeviceAdapter extends RecyclerView.Adapter<MoreConnectedDeviceAdapter.MoreDeviceViewHolder> {

    private List<ConnectedDeviceBean> list;
    private Context context;



    public MoreConnectedDeviceAdapter(List<ConnectedDeviceBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MoreDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_more_connect_device_layout,parent,false);
        return new MoreDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreDeviceViewHolder holder, int position) {
        ConnectedDeviceBean connectedDeviceBean = list.get(position);
        holder.itemMoreConnectNameTv.setText(connectedDeviceBean.getProductName());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    final class MoreDeviceViewHolder extends RecyclerView.ViewHolder{


        private TextView itemMoreConnectNameTv;
        private ImageView itemMoreConnectTypeImgView;
        //已经连接了的布局
        private LinearLayout itemMoreConnectStatusLayout;
        //未连接的布局
        private ConstraintLayout itemMoreConnectDisStatusLayout;

        //电量
        private TextView itemMoreConnectBatteryValue;

        //重新连接
        private TextView itemMoreConnectReConnTv;



        public MoreDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMoreConnectNameTv= itemView.findViewById(R.id.itemMoreConnectNameTv);
            itemMoreConnectTypeImgView = itemView.findViewById(R.id.itemMoreConnectTypeImgView);
            itemMoreConnectStatusLayout = itemView.findViewById(R.id.itemMoreConnectStatusLayout);
            itemMoreConnectDisStatusLayout = itemView.findViewById(R.id.itemMoreConnectDisStatusLayout);

            itemMoreConnectBatteryValue = itemView.findViewById(R.id.itemMoreConnectBatteryValue);
            itemMoreConnectReConnTv = itemView.findViewById(R.id.itemMoreConnectReConnTv);

        }
    }
}
