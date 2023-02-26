package com.app.fmate.ui.problemsFeedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.app.fmate.R;
import java.util.List;

public class FeedAddImgAdapter extends RecyclerView.Adapter<FeedAddImgAdapter.AddImgViewHolder> {

    private List<String> addList;
    private Context mContext;


    private AddFeedBackListener addFeedBackListener;

    public void setAddFeedBackListener(AddFeedBackListener addFeedBackListener) {
        this.addFeedBackListener = addFeedBackListener;
    }

    public FeedAddImgAdapter(List<String> addList, Context mContext) {
        this.addList = addList;
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AddImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_add_layout,parent,false);
        return new AddImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddImgViewHolder holder, int position) {
        String urlStr = addList.get(position);
        if(urlStr.contains("+")){   //添加的图片
            Glide.with(mContext).load(mContext.getResources().getDrawable(R.drawable.icon_feedback_add)).into(holder.addImgView);
        }else{
            Glide.with(mContext).load(urlStr).into(holder.addImgView);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                if(addFeedBackListener != null)
                    addFeedBackListener.onFeedItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return addList.size();
    }

    class AddImgViewHolder extends RecyclerView.ViewHolder{

        private ImageView addImgView;

        public AddImgViewHolder(@NonNull View itemView) {
            super(itemView);
            addImgView = itemView.findViewById(R.id.itemFeedBackImgVIew);
        }
    }


    public interface  AddFeedBackListener{
        void onFeedItemClick(int position);
    }
}
