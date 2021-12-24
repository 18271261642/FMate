package com.example.xingliansdk.adapter.node.provider;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.xingliansdk.R;
import com.example.xingliansdk.adapter.NodeTotalAdapter;
import com.example.xingliansdk.adapter.node.ExerciseRecordAdapter;
import com.example.xingliansdk.bean.node.DateRootNode;
import com.example.xingliansdk.view.DateUtil;
import com.shon.connector.utils.TLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 运动一级菜单
 */
public class DateRootNodeProvider extends BaseNodeProvider {
    RecyclerView ryTotal;

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_exrcise_record_date;
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, @Nullable BaseNode data, @NotNull List<?> payloads) {
        for (Object payload : payloads) {
            if (payload instanceof Integer && (int) payload == ExerciseRecordAdapter.EXPAND_COLLAPSE_PAYLOAD) {
                // 增量刷新，使用动画变化箭头
                setArrowSpin(helper, data, true);
            }
        }
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        TLog.Companion.error("再次回调");
        DateRootNode date = (DateRootNode) baseNode;
        ryTotal = baseViewHolder.getView(R.id.ryTotal);
        TLog.Companion.error("");
        //   baseViewHolder.setText(R.id.tvDate, DateUtil.getDate(DateUtil.YYYY_AND_MM,date.getDate()));
        baseViewHolder.setText(R.id.tvDate, date.getDateTime());
        ryTotal.setHasFixedSize(true);
        TLog.Companion.error("date++"+date.getTotalNode().size());
        if (ryTotal.getLayoutManager() == null) {
//            if( date.getTotalNode().size()>3)
//                ryTotal.setLayoutManager(new GridLayoutManager(context,3));
            ryTotal.setLayoutManager(new GridLayoutManager(context, date.getTotalNode().size()));
        }
        if (ryTotal.getAdapter() == null) {

            NodeTotalAdapter nestAdapter = new NodeTotalAdapter(date.getTotalNode());
            nestAdapter.setOnItemClickListener(null);
            nestAdapter.setOnItemChildClickListener(null);
            ryTotal.setAdapter(nestAdapter);
        }
        setArrowSpin(baseViewHolder, baseNode, false);
    }

    private void setArrowSpin(BaseViewHolder helper, BaseNode data, boolean isAnimate) {
        DateRootNode mDate = (DateRootNode) data;
        ryTotal = helper.getView(R.id.ryTotal);
        ImageView imageExpanded = helper.getView(R.id.imageExpanded);
        if (mDate.isExpanded()) {
            ryTotal.setVisibility(View.VISIBLE);
            imageExpanded.setRotation(270f);
        } else {
            ryTotal.setVisibility(View.GONE);
            imageExpanded.setRotation(90f);
        }

    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        getAdapter().expandOrCollapse(position, true, true, ExerciseRecordAdapter.EXPAND_COLLAPSE_PAYLOAD);
    }
}
