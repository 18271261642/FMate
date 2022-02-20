package com.example.xingliansdk.adapter.map;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xingliansdk.Config;
import com.example.xingliansdk.R;
import com.example.xingliansdk.bean.db.AmapRecordBean;
import com.example.xingliansdk.bean.db.AmapSportBean;
import com.example.xingliansdk.network.api.login.LoginBean;
import com.example.xingliansdk.utils.RecycleViewDivider;
import com.example.xingliansdk.utils.Utils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.utils.TLog;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.xingliansdk.Config.exercise.MILE;

/**
 * 运动记录adapter
 * Created by Admin
 * Date 2021/9/12
 */
public class AmapRecordAdapter extends RecyclerView.Adapter<AmapRecordAdapter.AmapRecordViewHolder> {


    private List<AmapRecordBean> sportBeanList;
    private Context mContext;
    int type;
    private LoginBean userInfo = Hawk.get(Config.database.USER_INFO, new LoginBean());

    //    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
//
//    private AmapOnItemClickListener amapOnItemClickListener;
//
//    public void setAmapOnItemClickListener(AmapOnItemClickListener amapOnItemClickListener) {
//        this.amapOnItemClickListener = amapOnItemClickListener;
//    }
    public void setType(int type) {
        this.type = type;
//        TLog.Companion.error("setType  =="+this.type);
        this.notifyDataSetChanged();
    }

    public AmapRecordAdapter(List<AmapRecordBean> sportBeanList, Context mContext, int type) {
        this.sportBeanList = sportBeanList;
        this.mContext = mContext;
        this.type = type;
    }

    @NonNull
    @Override
    public AmapRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_amap_sport_record_layout22, parent, false);
        return new AmapRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmapRecordViewHolder holder, int position) {
        AmapRecordBean amapRecordBean = sportBeanList.get(position);
        holder.itemMonthDayTv.setText(amapRecordBean.getMonthStr());
        String unit="公里";
        String walkCount=amapRecordBean.getWalkDistance();
        String runCount=amapRecordBean.getRunDistance();
        String cycleCount=amapRecordBean.getRideDistance();
        String distanceCount=amapRecordBean.getDistanceCount();
        if(userInfo==null||userInfo.getUserConfig().getDistanceUnit()==1) {
            unit = "英里";
            walkCount=  Utils.mul(Double.parseDouble(walkCount),MILE,2).toString();
            runCount=  Utils.mul(Double.parseDouble(runCount),MILE,2).toString();
            cycleCount=  Utils.mul(Double.parseDouble(cycleCount),MILE,2).toString();
            distanceCount=  Utils.mul(Double.parseDouble(distanceCount),MILE,3).toString();
        }
        if (type == 0) {
            holder.walkCountTvName.setText("步行("+unit+")");
            holder.runCountTvName.setText("跑步("+unit+")");
            holder.cycleCountTvName.setText("骑行("+unit+")");
            holder.walkCountTv.setText(walkCount);
            holder.runCountTv.setText(runCount);
            holder.cycleCountTv.setText(cycleCount);
        } else {
            holder.walkCountTvName.setText("距离("+unit+")");
            holder.runCountTvName.setText("热量(千卡)");
            holder.cycleCountTvName.setText("次数");
            holder.walkCountTv.setText(distanceCount);
            holder.runCountTv.setText(amapRecordBean.getCaloriesCount());
            holder.cycleCountTv.setText(amapRecordBean.getSportCount() + "");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.itemDetailRv.setLayoutManager(linearLayoutManager);
//        holder.itemDetailRv.addItemDecoration(
//              new RecycleViewDivider(
//                        mContext,
//                        LinearLayoutManager.HORIZONTAL,
//                        1,mContext.getResources().getColor(R.color.color_view)
//                )
//        );
        List<AmapSportBean> amList = amapRecordBean.getList();
        Collections.sort(amList, (amapSportBean, t1) -> t1.getEndSportTime().compareTo(amapSportBean.getEndSportTime()));
        AmapItemDetailAdapter amapItemDetailAdapter = new AmapItemDetailAdapter(amList, mContext);
        holder.itemDetailRv.setAdapter(amapItemDetailAdapter);


        boolean isShow = amapRecordBean.isShow();
        LinearLayout showLayout = holder.detailLayout;
        showLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);

        ImageView statusImg = holder.itemMonthImg;


        holder.itemMonthLayout.setOnClickListener(view -> {
            amapRecordBean.setShow(!amapRecordBean.isShow());
            if (amapRecordBean.isShow()) {
                statusImg.setRotation(270f);
            } else {
                statusImg.setRotation(90f);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return sportBeanList.size();
    }


    class AmapRecordViewHolder extends RecyclerView.ViewHolder {


        private ConstraintLayout itemMonthLayout;
        private TextView itemMonthDayTv;
        private ImageView itemMonthImg;
        private TextView walkCountTv, walkCountTvName;
        private TextView runCountTv, runCountTvName;
        private TextView cycleCountTv, cycleCountTvName;
        private LinearLayout detailLayout;

        private RecyclerView itemDetailRv;

        public AmapRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            itemMonthLayout = itemView.findViewById(R.id.itemRecyclerMonthLayout);
            itemMonthImg = itemView.findViewById(R.id.itemSportRecordImg);
            itemMonthDayTv = itemView.findViewById(R.id.itemSportRecordDateTv);
            walkCountTv = itemView.findViewById(R.id.itemRecordMonthWalkDistanceTv);
            runCountTv = itemView.findViewById(R.id.itemRecordMonthRunDistanceTv);
            cycleCountTv = itemView.findViewById(R.id.itemRecordMonthCycleDistanceTv);
            detailLayout = itemView.findViewById(R.id.itemRecordMonthDetailLayout);
            itemDetailRv = itemView.findViewById(R.id.itemRecordMonthRecyclerView);
            walkCountTvName = itemView.findViewById(R.id.itemRecordMonthWalkDistanceTvName);
            runCountTvName = itemView.findViewById(R.id.itemRecordMonthRunDistanceTvName);
            cycleCountTvName = itemView.findViewById(R.id.itemRecordMonthCycleDistanceTvName);

        }
    }

//
//    public interface AmapOnItemClickListener{
//        void onAmapItemClick(int position);
//    }

}
