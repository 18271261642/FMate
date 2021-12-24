package com.example.xingliansdk.network.api.setAllClock;

import com.shon.connector.bean.RemindTakeMedicineBean;
import java.util.List;

public class ClockListBean {

    /**
     * alarmClock : {"createTime":11111,"list":[{"characteristic":1,"hours":8,"min":0,"number":0,"specifiedTime":128,"unicode":"干活","unicodeType":0,"specifiedTimeDescription":"永不","endTime":1636416000,"mswitch":2,"creteTime":1111111}]}
     */

    //闹钟
    private AlarmClockDTO alarmClock;
    //日程
    private AlarmClockDTO schedule;
    //吃药提醒
    private takeMedicineDTO takeMedicine;

    public takeMedicineDTO getTakeMedicine() {
        return takeMedicine;
    }

    public void setTakeMedicine(takeMedicineDTO takeMedicine) {
        this.takeMedicine = takeMedicine;
    }

    public AlarmClockDTO getSchedule() {
        return schedule;
    }

    public void setSchedule(AlarmClockDTO schedule) {
        this.schedule = schedule;
    }



    public AlarmClockDTO getAlarmClock() {
        return alarmClock;
    }

    public void setAlarmClock(AlarmClockDTO alarmClock) {
        this.alarmClock = alarmClock;
    }
    public static class takeMedicineDTO {
        private long createTime;
        private List<RemindTakeMedicineBean> list;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public List<RemindTakeMedicineBean> getList() {
            return list;
        }

        public void setList(List<RemindTakeMedicineBean> list) {
            this.list = list;
        }
    }
    public static class AlarmClockDTO {
        /**
         * createTime : 11111
         * list : [{"characteristic":1,"hours":8,"min":0,"number":0,"specifiedTime":128,"unicode":"干活","unicodeType":0,"specifiedTimeDescription":"永不","endTime":1636416000,"mswitch":2,"creteTime":1111111}]
         */

        private long createTime;
        private List<ListDTO> list;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public static class ListDTO {
            /**
             * characteristic : 1
             * hours : 8
             * min : 0
             * number : 0
             * specifiedTime : 128
             * unicode : 干活
             * unicodeType : 0
             * specifiedTimeDescription : 永不
             * endTime : 1636416000
             * mswitch : 2
             * creteTime : 1111111
             */

            private int characteristic;
            private int hours;
            private int min;
            private int number;
            private int specifiedTime;
            private String unicode;
            private int unicodeType;
            private String specifiedTimeDescription;
            private long endTime;
            private int mSwitch;
            int year;
            int month;
            int day;

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }
//            private long creteTime;

            public int getCharacteristic() {
                return characteristic;
            }

            public void setCharacteristic(int characteristic) {
                this.characteristic = characteristic;
            }

            public int getHours() {
                return hours;
            }

            public void setHours(int hours) {
                this.hours = hours;
            }

            public int getMin() {
                return min;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public int getSpecifiedTime() {
                return specifiedTime;
            }

            public void setSpecifiedTime(int specifiedTime) {
                this.specifiedTime = specifiedTime;
            }

            public String getUnicode() {
                return unicode;
            }

            public void setUnicode(String unicode) {
                this.unicode = unicode;
            }

            public int getUnicodeType() {
                return unicodeType;
            }

            public void setUnicodeType(int unicodeType) {
                this.unicodeType = unicodeType;
            }

            public String getSpecifiedTimeDescription() {
                return specifiedTimeDescription;
            }

            public void setSpecifiedTimeDescription(String specifiedTimeDescription) {
                this.specifiedTimeDescription = specifiedTimeDescription;
            }

            public long getEndTime() {
                return endTime;
            }

            public void setEndTime(long endTime) {
                this.endTime = endTime;
            }

            public int getmSwitch() {
                return mSwitch;
            }

            public void setmSwitch(int mSwitch) {
                this.mSwitch = mSwitch;
            }

//            public int getCreteTime() {
//                return creteTime;
//            }
//
//            public void setCreteTime(int creteTime) {
//                this.creteTime = creteTime;
//            }
        }
    }
}
