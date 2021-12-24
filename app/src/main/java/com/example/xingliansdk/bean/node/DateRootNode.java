package com.example.xingliansdk.bean.node;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DateRootNode extends BaseExpandNode {

    private long date;
    private List<BaseNode> childNode;
    private List<BaseNode> totalNode;
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<BaseNode> getTotalNode() {
        return totalNode;
    }

    public void setTotalNode(List<BaseNode> totalNode) {
        this.totalNode = totalNode;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setChildNode(List<BaseNode> childNode) {
        this.childNode = childNode;
    }


    public DateRootNode(long date, List<BaseNode> childNode ) {
        this.date = date;
        this.childNode = childNode;
    }


    public DateRootNode(long date, List<BaseNode> childNode,List<BaseNode> totalNode) {
        this.date = date;
        this.childNode = childNode;
        this.totalNode=totalNode;
    }

    public DateRootNode(long date, List<BaseNode> childNode, List<BaseNode> totalNode, String dateTime) {
        this.date = date;
        this.childNode = childNode;
        this.totalNode = totalNode;
        this.dateTime = dateTime;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return childNode;
    }
}
