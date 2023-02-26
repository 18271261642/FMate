package com.app.fmate.adapter.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fmate.Config;
import com.app.fmate.R;
import com.app.fmate.bean.db.AmapSportBean;
import com.app.fmate.network.api.login.LoginBean;
import com.app.fmate.ui.fragment.map.newmap.AmapHistorySportActivity;
import com.app.fmate.utils.Utils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.utils.TLog;

import java.text.DecimalFormat;
import java.util.List;

import static com.app.fmate.Config.exercise.MILE;

/**
 * Created by Admin
 * Date 2021/9/17
 */
public class AmapItemDetailAdapter extends RecyclerView.Adapter<AmapItemDetailAdapter.ItemDetailViewHolder> {

    private static final String TAG = "AmapItemDetailAdapter";
    
    private List<AmapSportBean> list;
    private Context mContext;
    private LoginBean userInfo = Hawk.get(Config.database.USER_INFO, new LoginBean());
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public AmapItemDetailAdapter(List<AmapSportBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_amap_sport_record_layout, parent, false);
        return new ItemDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDetailViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AmapSportBean amapSportBean = list.get(position);
        Log.e(TAG,"------单个item="+amapSportBean.toString());
        try {
            int sportType = amapSportBean.getSportType();
            String unit=mContext.getString(R.string.string_km);
            Double dis=Utils.divi(Double.parseDouble(amapSportBean.getDistance()),1000.0,2);

            if(userInfo==null||userInfo.getUserConfig().getDistanceUnit()==1) {
                unit = mContext.getString(R.string.string_mile);
                dis= Double.valueOf(decimalFormat.format( Utils.mul(dis,MILE,3)));

            }
            holder.typeImg.setImageResource(mapSportTypeImg(sportType));
            holder.distanceTv.setText(dis + unit);
            holder.durationTv.setText(amapSportBean.getCurrentSportTime());
            holder.caloriesTv.setText(amapSportBean.getCalories() + mContext.getResources().getString(R.string.string_unit_kcal));
            String mapTime = amapSportBean.getEndSportTime();
            holder.currTimeTv.setText(Utils.formatCusTimeForDay(mapTime));
            //   holder.currTimeTv.setText(Utils.formatCusTimeForDay(mapTime)+"\n"+ Utils.formatCusTime(mapTime));
            if ((list.size()-1) > position)
                holder.viewColor.setVisibility(View.VISIBLE);
            else
                holder.viewColor.setVisibility(View.GONE);

            holder.constAll.setOnClickListener(view -> {
                AmapSportBean amapSportBean1 = list.get(position);
                TLog.Companion.error("==点击++" + new Gson().toJson(amapSportBean1));
                if (amapSportBean1 == null)
                    return;
                TLog.Companion.error("点击++" + new Gson().toJson(amapSportBean1));
                Intent intent = new Intent(mContext, AmapHistorySportActivity.class);
                intent.putExtra("sport_position", amapSportBean1);
                mContext.startActivity(intent);
            });
        } catch (Exception e) {
            TLog.Companion.error("??????" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemDetailViewHolder extends RecyclerView.ViewHolder {

        private ImageView typeImg;  //每天的item类型
        private TextView distanceTv;  //距离
        private TextView caloriesTv;  //卡路里
        private TextView durationTv;  //持续时间长
        private TextView currTimeTv;  //天
        private ConstraintLayout constAll;
        private View viewColor;

        public ItemDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            typeImg = itemView.findViewById(R.id.itemAmapSportTypeImg);
            distanceTv = itemView.findViewById(R.id.itemAmapSportDistanceTv);
            caloriesTv = itemView.findViewById(R.id.itemAmapSportCaloriesTv);
            durationTv = itemView.findViewById(R.id.itemAmapSportDurationTv);
            currTimeTv = itemView.findViewById(R.id.itemAmapSportCurrDayTimeTv);
            constAll = itemView.findViewById(R.id.constAll);
            viewColor = itemView.findViewById(R.id.view_color);
        }
    }

    private int mapSportTypeImg(int type) {
        if (type == 1)  //步行
            return R.mipmap.icon_walk;
        if (type == 2)
            return R.mipmap.icon_run;
        if (type == 3)
            return R.mipmap.icon_ride;
        return R.mipmap.icon_walk;
    }
}
