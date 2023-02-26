package com.app.fmate.adapter.node.provider;

import android.widget.TextView;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.app.fmate.Config;
import com.app.fmate.R;
import com.app.fmate.bean.node.TotalNode;
import com.shon.connector.utils.TLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExerciseRecordTotalNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_exrcise_record_total;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        if (baseNode == null)
            return;
        TLog.Companion.error("来了没有");
        TotalNode date = (TotalNode) baseNode;
        TextView tvTotalDistanceName = baseViewHolder.getView(R.id.tvTotalDistanceName);
        if (date.getType() == Config.exercise.WALK)
            tvTotalDistanceName.setText("步行");
        else if (date.getType() == Config.exercise.RUN)
            tvTotalDistanceName.setText("跑步");
        else if (date.getType() == Config.exercise.BICYCLE)
            tvTotalDistanceName.setText("自行车");
        else if (date.getType() == Config.exercise.MOUNTAIN_CLIMBING)
            tvTotalDistanceName.setText("爬山");
        baseViewHolder.setText(R.id.tvTotalDistance, date.getDistance());
    }
}
