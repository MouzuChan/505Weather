package com.example.l.myweather.util;

import java.util.Calendar;

/**
 * Created by L on 2016-04-24.
 */
public class TimeAndDate {
    private Calendar calendar;
    private CalendarUtil calendarUtil;
    private int year,month,day,hour,minute,week_int;


    public TimeAndDate(){
        calendar = Calendar.getInstance();
        calendarUtil = new CalendarUtil();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        week_int = calendar.get(Calendar.DAY_OF_WEEK);
    }

    public String getTodayWeek(){
        int week_int = calendar.get(Calendar.DAY_OF_WEEK);
        switch (week_int){
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
            default:
                return "";
        }
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getChineseDay(){
        return calendarUtil.getChineseMonth(year,month,day) + calendarUtil.getChineseDay(year,month,day);
    }

    public String[] getFullWeek(){
        String[] week_strings = new String[7];
        switch (week_int){
            case 1:
                week_strings[0] = "周日";
                week_strings[1] = "周一";
                week_strings[2] = "周二";
                week_strings[3] = "周三";
                week_strings[4] = "周四";
                week_strings[5] = "周五";
                week_strings[6] = "周六";
                break;
            case 2:
                week_strings[0] = "周一";
                week_strings[1] = "周二";
                week_strings[2] = "周三";
                week_strings[3] = "周四";
                week_strings[4] = "周五";
                week_strings[5] = "周六";
                week_strings[6] = "周日";
                break;
            case 3:
                week_strings[0] = "周二";
                week_strings[1] = "周三";
                week_strings[2] = "周四";
                week_strings[3] = "周五";
                week_strings[4] = "周六";
                week_strings[5] = "周日";
                week_strings[6] = "周一";
                break;
            case 4:
                week_strings[0] = "周三";
                week_strings[1] = "周四";
                week_strings[2] = "周五";
                week_strings[3] = "周六";
                week_strings[4] = "周日";
                week_strings[5] = "周一";
                week_strings[6] = "周二";
                break;
            case 5:
                week_strings[0] = "周四";
                week_strings[1] = "周五";
                week_strings[2] = "周六";
                week_strings[3] = "周日";
                week_strings[4] = "周一";
                week_strings[5] = "周二";
                week_strings[6] = "周三";
                break;
            case 6:
                week_strings[0] = "周五";
                week_strings[1] = "周六";
                week_strings[2] = "周日";
                week_strings[3] = "周一";
                week_strings[4] = "周二";
                week_strings[5] = "周三";
                week_strings[6] = "周四";
                break;
            case 7:
                week_strings[0] = "周六";
                week_strings[1] = "周日";
                week_strings[2] = "周一";
                week_strings[3] = "周二";
                week_strings[4] = "周三";
                week_strings[5] = "周四";
                week_strings[6] = "周五";
                break;
        }
        return week_strings;
    }

    public String[] getDateStrings(){
        String[] date_strings = new String[7];
        Calendar c = calendar;
        int d = calendar.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i<7; i++){
            c.set(Calendar.DAY_OF_YEAR,d);
            date_strings[i] = c.get(Calendar.MONTH) + 1 + "/" + c.get(Calendar.DATE);
            d++;
        }
        return date_strings;
    }
    /*
    static TimeAndDate getInstance(){
        if (timeAndDate == null){
            timeAndDate = new TimeAndDate();
        } else {
            timeAndDate.calendar = Calendar.getInstance();
            timeAndDate.year = timeAndDate.calendar.get(Calendar.YEAR);
            timeAndDate.month = timeAndDate.calendar.get(Calendar.MONTH) + 1;
            timeAndDate.day = timeAndDate.calendar.get(Calendar.DAY_OF_MONTH);
            timeAndDate.hour = timeAndDate.calendar.get(Calendar.HOUR_OF_DAY);
            timeAndDate.minute = timeAndDate.calendar.get(Calendar.MINUTE);
            timeAndDate.week_int = timeAndDate.calendar.get(Calendar.DAY_OF_WEEK);
        }
        return timeAndDate;
    }
    */
}
