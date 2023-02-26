package com.app.fmate.network.api.bloodPressureView;

import java.util.List;

public class BloodPressureVoBean {

    private List<ListDTO> list;

    //是否需要校准血压
    private boolean calibrationRequired;

    //校准信息
    private String calibrationReason;

    public boolean isCalibrationRequired() {
        return calibrationRequired;
    }

    public void setCalibrationRequired(boolean calibrationRequired) {
        this.calibrationRequired = calibrationRequired;
    }

    public String getCalibrationReason() {
        return calibrationReason;
    }

    public void setCalibrationReason(String calibrationReason) {
        this.calibrationReason = calibrationReason;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * createTime : 2021-08-04 16:16:15
         * stampCreateTime : 1628064975
         * systolicPressure : 150
         *     "dataSource": 0,         //数据来源     0手动输入    1惊帆
         * diastolicPressure : 80
         */

        private String createTime;
        private long stampCreateTime;
        private int systolicPressure;
        private int diastolicPressure;
        private int dataSource; //来源

        public int getDataSource() {
            return dataSource;
        }

        public void setDataSource(int dataSource) {
            this.dataSource = dataSource;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
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

        public int getDiastolicPressure() {
            return diastolicPressure;
        }

        public void setDiastolicPressure(int diastolicPressure) {
            this.diastolicPressure = diastolicPressure;
        }
        
    }
}
