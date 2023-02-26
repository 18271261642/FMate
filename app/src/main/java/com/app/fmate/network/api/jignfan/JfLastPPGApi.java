package com.app.fmate.network.api.jignfan;

import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/5/25
 */
public class JfLastPPGApi implements IRequestApi {
    @NonNull
    @Override
    public String getApi() {
        return "/health/get_blood_pressure";
    }

    private String date;

    public JfLastPPGApi getLastData(String day){
        this.date = day;
        return this;
    }



    public static class PPGBean  {


        /**
         * createTime : 2022-05-24 21:16:00
         * dataSource : 0
         * diastolicPressure : 110
         * stampCreateTime : 1653398160
         * systolicPressure : 180
         */

        private String createTime;
        private int dataSource;
        private int diastolicPressure;
        private long stampCreateTime;
        private int systolicPressure;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getDataSource() {
            return dataSource;
        }

        public void setDataSource(int dataSource) {
            this.dataSource = dataSource;
        }

        public int getDiastolicPressure() {
            return diastolicPressure;
        }

        public void setDiastolicPressure(int diastolicPressure) {
            this.diastolicPressure = diastolicPressure;
        }

        public long getStampCreateTime() {
            return stampCreateTime;
        }

        public void setStampCreateTime(long stampCreateTime) {
            this.stampCreateTime = stampCreateTime;
        }

        public int getSystolicPressure() {
            return systolicPressure;
        }

        public void setSystolicPressure(int systolicPressure) {
            this.systolicPressure = systolicPressure;
        }
    }
}
