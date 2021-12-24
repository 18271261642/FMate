package com.shon.connector.bean;

import java.util.List;

public class RemindTakeMedicineBean {
    /**
     * 闹钟编号
     */
    public int number;
    /**
     * 开关
     */
    public int mSwitch; //0默认 1关 2开
    /**
     * 开始时间戳
     */
    public long startTime;
    /**
     * 结束时间戳
     */
    public long endTime;
    /**
     * 提醒周期
     */
    public int ReminderPeriod;

    public List<ReminderGroup> groupList;
    /**
     * unicode 编码标题
     */
    public String unicodeTitle;
    /**
     * unicode 编码内容
     */
    public String unicodeContent;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSwitch() {
        return mSwitch;
    }

    public void setSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getReminderPeriod() {
        return ReminderPeriod;
    }

    public void setReminderPeriod(int reminderPeriod) {
        ReminderPeriod = reminderPeriod;
    }

    public List<ReminderGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<ReminderGroup> groupList) {
        this.groupList = groupList;
    }

    public String getUnicodeTitle() {
        return unicodeTitle;
    }

    public void setUnicodeTitle(String unicodeTitle) {
        this.unicodeTitle = unicodeTitle;
    }

    public String getUnicodeContent() {
        return unicodeContent;
    }

    public void setUnicodeContent(String unicodeContent) {
        this.unicodeContent = unicodeContent;
    }

    public class ReminderGroup {
        /**
         * 提醒组时
         */
        int groupHH;
        /**
         * 提醒组分
         */
        int groupMM;

        public int getGroupHH() {
            return groupHH;
        }

        public void setGroupHH(int groupHH) {
            this.groupHH = groupHH;
        }

        public int getGroupMM() {
            return groupMM;
        }

        public void setGroupMM(int groupMM) {
            this.groupMM = groupMM;
        }

        public ReminderGroup() {
        }

        public ReminderGroup(int groupHH, int groupMM) {
            this.groupHH = groupHH;
            this.groupMM = groupMM;

        }
    }

}
