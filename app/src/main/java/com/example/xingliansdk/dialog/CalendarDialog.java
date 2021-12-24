//package com.example.xingliansdk.dialog;
//
//import android.content.Context;
//import android.content.DialogInterface;
//
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//
///**
// * 功能:日历控制器弹窗
// */
//
//public class CalendarDialog {
//    private BaseDialog dialog;
//    private Context context;
//    private CustomCalendar customCalendar;
//    private OnCalendarClickListener listener;
//
//
//    public CalendarDialog(Context context) {
//        this.context = context;
//    }
//
//    public void show() {
//        if (dialog == null) {
//            int width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.8f);
//            dialog = new BaseDialog.Builder(context)
//                    .setContentView(R.layout.dialog_calendar)
//                    .setWidth(width)
//                    .setText(R.id.tvCalendarDialogTitle, DateUtil.getCurrentDate(context.getString(R.string.content_calendar_format)))
//
//                    .setOnClickListener(R.id.ivCalendarDialogLeft, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            customCalendar.monthChange(-1);
//                            updateTitleDate();
//                            updateEveryDaySportTargetMessage();
//                            if (listener != null) {
//                                listener.onMonthChanged(customCalendar.getCurrentCalendar());
//                            }
//                        }
//                    })
//                    .setOnClickListener(R.id.ivCalendarDialogRight, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            long curMonth = DateUtil.convertCalendarToLong(customCalendar.getCurrentCalendar());
//                            long maxMonth = DateUtil.getCurrentCalendarBegin().getTimeInMillis();
//                            if (curMonth <= maxMonth) {
//                                customCalendar.monthChange(1);
//                                updateTitleDate();
//                                updateEveryDaySportTargetMessage();
//                                if (listener != null) {
//                                    listener.onMonthChanged(customCalendar.getCurrentCalendar());
//                                }
//                            } else {
//                                SNToast.toast(context.getString(R.string.content_can_not_choose_future));
//                            }
//                        }
//                    })
//                    .show();
//            initCalendar();
//        } else {
//            dialog.show();
//        }
//    }
//
//    public void dismiss() {
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//    }
//
//    public void setDate(Calendar date) {
//        if (customCalendar != null) {
//            customCalendar.setSelectedDate(date);
//            updateEveryDaySportTargetMessage();
//            updateTitleDate();
//            if (dialog != null) {
//                dialog.setText(R.id.tvCalendarTitleText, CalendarUtil.getDateAndWeekString(context,date));
//            }
//        }
//    }
//
//
//    public Calendar getDate() {
//        return customCalendar.getCurrentCalendar();
//    }
//
//    private void updateTitleDate() {
//        dialog.setText(R.id.tvCalendarDialogTitle, DateUtil.getDate(context.getString(R.string.content_calendar_title), customCalendar.getCurrentCalendar()));
//
//    }
//
//
//    /**
//     * 初始化日历
//     */
//    private void initCalendar() {
//        customCalendar = dialog.findViewById(R.id.cCalendar);
//        updateEveryDaySportTargetMessage();
//
//        customCalendar.setOnClickListener(new CustomCalendar.OnClickListener() {
//            @Override
//            public void onDayClick(Day day) {
//                int selectDay = day.getDay();
//                int curMonth = DateUtil.getMonth(customCalendar.getCurrentCalendar());
//                int maxMonth = DateUtil.getMonth(DateUtil.getCurrentCalendar());
//                if (curMonth == maxMonth) {
//                    int mToday = DateUtil.getDay(DateUtil.getCurrentCalendar());
//                    if (mToday < selectDay)//选择超出了今天
//                    {
//                        SNToast.toast(context.getString(R.string.content_can_not_choose_future));
//                        return;
//                    }
//                }
//                dialog.setText(R.id.tvCalendarTitleText, CalendarUtil.getDateAndWeekString(context, day.getDate()));
//                dismiss();
//                if (listener != null) {
//                    Calendar calendar = DateUtil.getCurrentCalendar();
//                    calendar.setTime(day.getDate());
//                    listener.onDayChanged(calendar);
//                }
//            }
//        });
//
//    }
//
//
//    public interface OnCalendarClickListener {
//        /**
//         * 日期选择改变
//         *
//         * @param date
//         */
//        void onDayChanged(Calendar date);
//
//        /**
//         * 月切换
//         *
//         * @param date
//         */
//        void onMonthChanged(Calendar date);
//    }
//
//
//    public void setCalendarClickListener(OnCalendarClickListener listener) {
//        this.listener = listener;
//    }
//
//    /**
//     * 刷新每一天的列表数据
//     */
//    private void updateEveryDaySportTargetMessage() {
//        SNAsyncTask.execute(new SNVTaskCallBack() {
//            private List<DayCompletion> list = new ArrayList<>();
//
//            @Override
//            public void run() throws Throwable {
//                int year = DateUtil.getYear(customCalendar.getCurrentCalendar());
//                int month = DateUtil.getMonth(customCalendar.getCurrentCalendar());
//
//                List<SportBean> sportBeans = SportDao.get(SportDao.class).queryForMonth(AppUserUtil.getUser().getUser_id(), year, month);
//                for (SportBean sportBean : sportBeans) {
//                    int day = DateUtil.getDay(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, sportBean.getDate()));
//                    //TODO 这个可能有问题, 抗抗说 之前的设备可能返回0, 代表以前的用户旧WellGO 用户没传目标值,此时默认以 当前用户设置的目标为准
//                    int stepTarget = sportBean.getStepTarget() > 0 ? sportBean.getStepTarget() : AppUserUtil.getUser().getTarget_step();
//
//                    list.add(new DayCompletion(day, sportBean.getStepTotal(), stepTarget));
//                }
//
//            }
//
//            @Override
//            public void done() {
//                customCalendar.drawProgress(list);
//            }
//        });
//    }
//
//}
