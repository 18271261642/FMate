package com.app.fmate.adapter.node.provider;

import android.widget.ImageView;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.app.fmate.Config;
import com.app.fmate.R;
import com.app.fmate.bean.node.ItemExerciseRecordNode;
import com.shon.connector.utils.TLog;
import com.app.fmate.view.DateUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 运动二级菜单
 */
public class ExerciseRecordNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_exrcise_record_index;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        if (baseNode == null)
            return;
        TLog.Companion.error("来了没有");
        ItemExerciseRecordNode date = (ItemExerciseRecordNode) baseNode;
        ImageView imgHead = baseViewHolder.getView(R.id.imgHead);
        if (date.getType() == Config.exercise.WALK)
            imgHead.setImageResource(R.mipmap.icon_walk);
        else if (date.getType() == Config.exercise.RUN)
            imgHead.setImageResource(R.mipmap.icon_run);
        else if (date.getType() == Config.exercise.BICYCLE)
            imgHead.setImageResource(R.mipmap.icon_ride);
        else if (date.getType() == Config.exercise.MOUNTAIN_CLIMBING)
            imgHead.setImageResource(R.mipmap.icon_ride);
        baseViewHolder.setText(R.id.tvDistance, date.getDistance()+"公里");
        baseViewHolder.setText(R.id.tvTime, DateUtil.getTime(date.getTime()));
        baseViewHolder.setText(R.id.tvCalories, date.getCalories()+"千卡");
        baseViewHolder.setText(R.id.tvDate, DateUtil.getDate(DateUtil.MM_AND_DD, date.getDate()));
    }
}
