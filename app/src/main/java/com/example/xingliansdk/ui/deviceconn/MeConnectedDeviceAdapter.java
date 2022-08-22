package com.example.xingliansdk.ui.deviceconn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xingliansdk.R;
import com.google.gson.Gson;
import com.shon.connector.utils.TLog;

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
public class MeConnectedDeviceAdapter extends RecyclerView.Adapter<MeConnectedDeviceAdapter.MoreDeviceViewHolder> {

    private List<ConnectedDeviceBean> list;
    private Context context;

    private OnMeAdapterItemClick onMeAdapterItemClick;


    public void setOnMeAdapterItemClick(OnMeAdapterItemClick onMeAdapterItemClick) {
        this.onMeAdapterItemClick = onMeAdapterItemClick;
    }

    public MeConnectedDeviceAdapter(List<ConnectedDeviceBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MoreDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mine_connected_layout,parent,false);
        return new MoreDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreDeviceViewHolder holder, int position) {
        if(list == null)
            return;
        ConnectedDeviceBean connectedDeviceBean = list.get(position);
        if(connectedDeviceBean == null)
            return;

        TLog.Companion.error("-------item的多多="+new Gson().toJson(connectedDeviceBean));

        holder.itemMoreConnectNameTv.setText(connectedDeviceBean.getProductName());
        ImageView imageView = holder.itemMoreConnectTypeImgView;
//        Glide.with(context).clear(imageView);
//        Glide.with(context).load(connectedDeviceBean.)

        holder.itemMeHomeStatusTv.setText(connectedDeviceBean.isConnected() ? "已连接" : "未连接");
        holder.itemMeConnBatteryImg.setText(connectedDeviceBean.isConnected() ? (connectedDeviceBean.getBattery()+"%") : "重连连接");

        if(connectedDeviceBean.getProductName().toLowerCase(Locale.ROOT).contains("ring")){
            Glide.with(context).load(R.drawable.ic_place_ring).into(holder.itemMoreConnectTypeImgView);
        }else{
            Glide.with(context).load(R.drawable.ic_place_watch).into(holder.itemMoreConnectTypeImgView);
        }

        if(list.size() == 1){
            holder.itemMeConnectedTv.setText("添加设备");
        }else{
            holder.itemMeConnectedTv.setText(connectedDeviceBean.isConnected() ? "更多设备" : "添加设备");
        }

        int itemPosition = holder.getLayoutPosition();
        //重新连接文字，未连接显示，连接后隐藏
        TextView reConnTv = holder.tmeItemMeCView;
        reConnTv.setVisibility(connectedDeviceBean.isConnected() ? View.INVISIBLE : View.VISIBLE);
        if(!connectedDeviceBean.isConnected()){
            reConnTv.setText(connectedDeviceBean.getConnstatusEnum() == ConnstatusEnum.CONNECTING ? "连接中.." : "重新连接");
        }


        //删除，连接成功隐藏，未连接显示
        TextView deleteTv = holder.meWatchDeleteTv;
        deleteTv.setVisibility(connectedDeviceBean.isConnected() ? View.INVISIBLE : View.VISIBLE);


        holder.meConnWatchBatteryLayout.setVisibility(connectedDeviceBean.isConnected() ? View.VISIBLE : View.GONE);


        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMeAdapterItemClick !=null)
                    onMeAdapterItemClick.onItemDeleteClick(itemPosition);
            }
        });

        //重新连接的点击
        reConnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMeAdapterItemClick !=null)
                    onMeAdapterItemClick.onItemReConnClick(itemPosition);
            }
        });


        //删除的点击

        //整个item的点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMeAdapterItemClick!= null)
                    onMeAdapterItemClick.onItemClick(itemPosition);
            }
        });

    }

    //最大两个
    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : (Math.min(list.size(), 2));
    }



    final class MoreDeviceViewHolder extends RecyclerView.ViewHolder{


        private TextView itemMoreConnectNameTv;
        private ImageView itemMoreConnectTypeImgView;
        //已经连接了的布局
        private LinearLayout itemMoreConnectStatusLayout;
        //未连接的布局
        private ConstraintLayout itemMoreConnectDisStatusLayout;

        //电量
        private TextView itemMeConnBatteryImg;

        //连接状态
        private TextView itemMeHomeStatusTv;

        //添加设备或更多设备，已连接显示更多设备，未连接显示添加设备，只有一个item是显示添加设备
        private TextView itemMeConnectedTv;

        //重新连接，点击后判断条件是否满足，重新连接
        private TextView tmeItemMeCView;

        //删除
        private TextView meWatchDeleteTv;

        //电量的布局，已连接显示，未连接不显示
        private LinearLayout meConnWatchBatteryLayout;


        public MoreDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMoreConnectNameTv= itemView.findViewById(R.id.itemMeConnectedTypeNameTv);
            itemMoreConnectTypeImgView = itemView.findViewById(R.id.itemMeTypeImgView);
            itemMeConnectedTv = itemView.findViewById(R.id.itemMeConnectedTv);

            meWatchDeleteTv = itemView.findViewById(R.id.meWatchDeleteTv);

            itemMeHomeStatusTv = itemView.findViewById(R.id.itemMeHomeStatusTv);
//            itemMoreConnectDisStatusLayout = itemView.findViewById(R.id.itemMoreConnectDisStatusLayout);
//
            itemMeConnBatteryImg = itemView.findViewById(R.id.itemMeConnectBatteryValue);
//            itemMoreConnectReConnTv = itemView.findViewById(R.id.itemMoreConnectReConnTv);


            tmeItemMeCView = itemView.findViewById(R.id.tmeItemMeCView);

            meConnWatchBatteryLayout = itemView.findViewById(R.id.meConnWatchBatteryLayout);

        }
    }


    public interface OnMeAdapterItemClick{
        //整个item的点击
        void onItemClick(int position);

        //重新连接的点击
        void onItemReConnClick(int position);

        //删除设备的点击
        void onItemDeleteClick(int position);
    }
}
