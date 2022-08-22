package com.example.xingliansdk.ui.deviceconn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xingliansdk.Config;
import com.example.xingliansdk.R;
import com.example.xingliansdk.XingLianApplication;
import com.example.xingliansdk.bean.DevicePropertiesBean;
import com.orhanobut.hawk.Hawk;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 所以已经绑定过的设备
 * Created by Admin
 * Date 2022/7/27
 */
public class MoreConnectedDeviceAdapter extends RecyclerView.Adapter<MoreConnectedDeviceAdapter.MoreDeviceViewHolder> {

    private List<ConnectedDeviceBean> list;
    private Context context;


    private OnMoreConnDeleteListener onMoreConnDeleteListener;

    public void setOnMoreConnDeleteListener(OnMoreConnDeleteListener onMoreConnDeleteListener) {
        this.onMoreConnDeleteListener = onMoreConnDeleteListener;
    }

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

        ImageView typeImg = holder.itemMoreConnectTypeImgView;


        if(connectedDeviceBean.getProductName().toLowerCase(Locale.ROOT).contains("ring")){
            Glide.with(context).load(R.drawable.ic_place_ring).into(typeImg);
        }else{
            Glide.with(context).load(R.drawable.ic_place_watch).into(typeImg);
        }

        //已经连接的Mac
        String mac = Hawk.get("address");
        boolean isConn = mac != null && mac.toLowerCase(Locale.ROOT).equals(connectedDeviceBean.getMac()) && XingLianApplication.mXingLianApplication.getDeviceConnStatus();

        holder.itemMoreConnStatusTv.setText(isConn? "已连接" : "未连接");

        if(isConn){
            holder.itemMoreConnectDelTv.setVisibility(View.GONE);

            DevicePropertiesBean devicePropertiesBean = Hawk.get(
                    Config.database.DEVICE_ATTRIBUTE_INFORMATION,
                    new DevicePropertiesBean(0, 0, 0, 0)
            );

            if(devicePropertiesBean != null){
                holder.itemMoreConnectBatteryValue.setText(devicePropertiesBean.getElectricity()+"%");
            }
        }else{
            holder.itemMOreConnBatteryImg.setVisibility(View.GONE);
            holder.itemMoreConnectBatteryValue.setVisibility(View.GONE);

            holder.itemMoreConnectDelTv.setVisibility(View.VISIBLE);
        }



        holder.itemMoreConnectDelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMoreConnDeleteListener != null)
                    onMoreConnDeleteListener.deleteItem(holder.getLayoutPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    final class MoreDeviceViewHolder extends RecyclerView.ViewHolder{


        private TextView itemMoreConnectNameTv;
        private ImageView itemMoreConnectTypeImgView;

        //未连接的布局
        private ConstraintLayout itemMoreConnectDisStatusLayout;

        private ImageView itemMOreConnBatteryImg;

        //电量
        private TextView itemMoreConnectBatteryValue;

        //重新连接
        private TextView itemMoreConnectReConnTv;

        //是否连接
        private TextView itemMoreConnStatusTv;

        //删除设备
        private TextView itemMoreConnectDelTv;


        public MoreDeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            itemMoreConnectDelTv = itemView.findViewById(R.id.itemMoreConnectDelTv);

            itemMoreConnectNameTv= itemView.findViewById(R.id.itemMoreConnectNameTv);
            itemMoreConnectTypeImgView = itemView.findViewById(R.id.itemMoreConnectTypeImgView);
//            itemMoreConnectStatusLayout = itemView.findViewById(R.id.itemMoreConnectStatusLayout);
            itemMoreConnectDisStatusLayout = itemView.findViewById(R.id.itemMoreConnectDisStatusLayout);

            itemMoreConnectBatteryValue = itemView.findViewById(R.id.itemMoreConnectBatteryValue);
            itemMoreConnectReConnTv = itemView.findViewById(R.id.itemMoreConnectReConnTv);

            itemMoreConnStatusTv = itemView.findViewById(R.id.itemMoreConnStatusTv);
            itemMOreConnBatteryImg = itemView.findViewById(R.id.itemMOreConnBatteryImg);

        }
    }

    private interface OnMoreConnDeleteListener{
        //删除设备
        void deleteItem(int position);
    }
}
