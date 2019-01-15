package com.zmm.twserverpc_zbd.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
    
    /** 通过除乘1000过滤毫秒数——*/
    public static final long FILTER_MILLISECOND = 1000;
    
    /** 5分钟对应毫秒*/
    public static final long FIVE_MINUTE = 5*60*1000;
    
    /** 单天对应毫秒数*/
    public static final long TIME_IN_MILLS_DAY = 24 * 60 * 60 * 1000;
    /** 1小时对应毫秒数*/
    public static final long TIME_IN_MILLS_HOUR = 60 * 60 * 1000;
    /** 1分钟对应毫秒数*/
    public static final long TIME_IN_MILLS_MINUTE = 60 * 1000;

    public static final long TIME_IN_HALF_HOURS = 30*60 * 1000;

    /** 转换为HH:mm*/
    public static final SimpleDateFormat H2M2 = new SimpleDateFormat("HH:mm");

    /** 转换为HH:mm*/
    public static final SimpleDateFormat H2M2S2 = new SimpleDateFormat("HH:mm:ss");

    /** 转换为yyyy-MM-dd*/
    public static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat YMDH2M2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** 转换为yyyy-MM-dd*/
    public static final SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy年MM月dd日");

    /** 转换为MM月dd日*/
    public static final SimpleDateFormat MD = new SimpleDateFormat("MM月dd日");

    /**
     * 通过Calendar得到当前毫秒
     * 毫秒转换为当天具体的H2M2字符串
     * 再将H2M2字符串转换为对应毫秒 
     * @return
     */
    public static long getCurrentM_H2M2_Millis(){
        return getH2M2_Millis(H2M2.format(Calendar.getInstance().getTimeInMillis()));
    }
    
    /** 
     * 通过Calendar得到当前时间毫秒对应的H2M2
     * @return HH:mm
     */
    public static String getCurrentTime_H2M2(){
        return H2M2.format(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 通过Calendar得到当前时间毫秒对应的H2M2
     * @return HH:mm
     */
    public static String getCurrentTime_H2M2(long time){
        return H2M2.format(time);
    }

    /**
     * 通过当前时间秒得到对应的H2M2S2
     * @return HH:mm:ss
     */
    public static String getCurrentTime_H2M2S2(int time){
        int hour = time/3600;
        int min = (time - hour * 3600)/60;
        int second = time - hour * 3600 - min * 60;
        String result = "";
        if(hour<10)
        {
            result = "0";
        }
        result += String.valueOf(hour)+":";
        if(min<10)
        {
            result +="0";
        }
        result += String.valueOf(min)+":";
        if(second<10)
        {
            result +="0";
        }
        result += String.valueOf(second);
        return result;
    }

    /**
     * 通过当前时间秒得到对应的H2M2S2
     * @return HH:mm:ss
     */
    public static String getCurrentTime_M2S2(int time){
        int hour = time / 3600;
        int min = (time - hour * 3600)/60;
        int second = time - hour * 3600 - min * 60;
        String result = "";
        if(hour > 0)
        {
            result ="0'00''";
        }
        else
        {
            result = String.valueOf(min)+"'";
            if(second < 10)
            {
                result +="0";
            }
            result += String.valueOf(second)+"''";
        }
        return result;
    }

    public static long getLongTime(String currenTime, String beginTime){
        long result = 0;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间
        try {
            result = (d.parse(currenTime).getTime() - d.parse(beginTime)
                    .getTime()) / 1000;// 当前时间减去测试时间
            // 这个的除以1000得到秒，相应的60000得到分，3600000得到小时
            Log.i("---->","当前时间减去测试时间=" + result + "秒");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 通过Calendar得到当前毫秒对应YMD
     * @return yyyy-MM-dd
     * */
    public static String getCurrentTime_YMD(){
        return YMD.format(Calendar.getInstance().getTimeInMillis());
    }

    public static String getCurrentTime_YMDHM(){
        return YMDHM.format(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 通过Calendar得到当前时间毫秒对应的H2M2
     * @return HH:mm
     */
    public static String getCurrentTime_YMDH2M2(){
        return YMDH2M2.format(Calendar.getInstance().getTimeInMillis());
    }

    //"yyyy-MM-dd HH:mm:ss"
    public static String getYMDH2M2(){
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        String year = calendar.get(Calendar.YEAR)+"";
        String month = calendar.get(Calendar.MONTH)+1+"";
        String day = calendar.get(Calendar.DAY_OF_MONTH)+"";
        String hour = calendar.get(Calendar.HOUR_OF_DAY)+"";
        String minute = calendar.get(Calendar.MINUTE)+"";
        String second = calendar.get(Calendar.SECOND)+"";
        if (Integer.valueOf(month) < 10){
            month = "0" + month;
        }
        if (Integer.valueOf(day) < 10){
            day = "0" + day;
        }
        if (Integer.valueOf(second) < 10){
            second = "0" + second;
        }
        String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        return time;
    }

    /**
     * 通过毫秒数对应YMD
     * @return yyyy-MM-dd
     * */
    public static String getCurrentTime_YMD(long time){
        return YMD.format(time);
    }
    
    /**
     * 得到当天00：00开始的毫秒
     * (这里没有过滤位数3位的毫秒值)
     * @return
     */
    public static long getDayMillis(){
        return getTimeInMillsTodayBegin();
    }
    
    /**
     * 得到当天00：00开始的毫秒
     * (有过滤位数3位的毫秒值)
     * @return
     */
    public static long getDayMillis_Optimize(){
        return getTimeInMillsTodayBegin()/FILTER_MILLISECOND*FILTER_MILLISECOND;
    }
    
    /**
     * 通过H2M2(HH:mm)得到对应的毫秒数据
     * @param dayTime
     * @return long : 58620000
     */
    public static long getH2M2_Millis(String dayTime){
        long time = 0;
        String[] times = dayTime.split(":");
        String hour = times[0];
        String minute = times[1];
        time = Integer.valueOf(hour)*TIME_IN_MILLS_HOUR + Integer.valueOf(minute)*TIME_IN_MILLS_MINUTE;
        return time;
    }

    /**
     * 通过H2M2(HH:mm)得到 hour 对应的int类型值
     * 例如：10:04:15.250 PM the {@code HOUR_OF_DAY} is 22
     * @param dayTime
     * @return
     */
    public static Integer getH2Str(String dayTime){
        String[] times = dayTime.split(":");
        String hour = times[0];
        return Integer.valueOf(hour);
    }

    /**
     * 通过H2M2(HH:mm)得到 hour 对应的int类型值
     * 例如：at 10:04:15.250 PM the {@code MINUTE} is 4.
     * @param dayTime
     * @return
     */
    public static Integer getM2Str(String dayTime){
        String[] times = dayTime.split(":");
        String minute = times[1];
        return Integer.valueOf(minute);
    }

    /**
     * 通过Y4H2M2(yyyy-MM-dd)得到 yyyy 对应的int类型值
     * 例如：
     * @param year_str
     * @return
     */
    public static String getMMDD(String year_str){
        String[] times = year_str.split("-");
        return times[1]+"月"+times[2]+"日";
    }

    /**
     * 通过Y4H2M2(yyyy-MM-dd)得到 yyyy 对应的int类型值
     * 例如：
     * @param year_str
     * @return
     */
    public static Integer getY_Y_Integer(String year_str){
        String[] times = year_str.split("-");
        String year = times[0];
        return Integer.valueOf(year);
    }

    /**
     * 通过Y4H2M2(yyyy-MM-dd)得到 MM 对应的int类型值
     * @param year_str
     * @return
     */
    public static Integer getY_M2_Integer(String year_str){
        String[] times = year_str.split("-");
        String hour = times[1];
        return Integer.valueOf(hour) - 1;
    }

    /**
     * 通过Y4H2M2(yyyy-MM-dd)得到 dd 对应的int类型值
     * @param year_str
     * @return
     */
    public static Integer getY_D2_Integer(String year_str){
        String[] times = year_str.split("-");
        String minute = times[2];
        return Integer.valueOf(minute);
    }




    /**
     * 将HH:mm对应毫秒数据转换为H2M2字符串
     * @param dayTime
     * @return HH:mm
     */
    public static String getH2M2(long dayTime) {
        long hours = dayTime / TIME_IN_MILLS_HOUR;
        long minutes = dayTime % TIME_IN_MILLS_HOUR / TIME_IN_MILLS_MINUTE;
        String hour = hours < 10 ? 0 + "" + hours : String.valueOf(hours);
        String minute = minutes < 10 ? 0 + "" + minutes : String.valueOf(minutes);
        return hour + ":" + minute;
    }
    
    /**
     * 当天0时0分0秒开始对应毫秒
     * @return
     */
    public static long getTimeInMillsTodayBegin() {
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        long currentDayTime = date.getTime();
        return currentDayTime;
    }

    /**
     * 获取当前年
     */
    public static  int getCurrentYear(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.YEAR);
    }

    /**
     * 获取当前年
     */
    public static  int getCurrentMonth(){
        Calendar a= Calendar.getInstance();
        return a.get(Calendar.MONTH)+1;
    }

    /**
     * 获取yyyy
     */
    public static  long getFormatDataMills(String yyyyMMdd){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(yyyyMMdd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * 根据毫秒数获取年
     * @param time
     * @return
     */
    public static int getYearByMills(long time){
        if(time > 0){
            String[] ymd = DateUtil.getCurrentTime_YMD(time).split("-");
            String ret = ymd[0];
            return Integer.parseInt(ret);
        }
        return  2010;
    }

    /**
     * 根据毫秒数获取年
     * @param time
     * @return
     */
    public static String getYearByMillsyyyy(long time){
        if(time > 0){
            String[] ymd = DateUtil.getCurrentTime_YMD(time).split("-");
            String ret = ymd[0];
            return ret;
        }
        return  "2010";
    }

    /**
     * 根据毫秒数获取月
     * @param time
     * @return
     */
    public static String getMonthByMillsMM(long time){
        if(time > 0){
            String[] ymd = DateUtil.getCurrentTime_YMD(time).split("-");
            String ret = ymd[1];
            return ret;
        }
        return  "1";
    }

    /**
     * 根据毫秒数获取月
     * @param time
     * @return
     */
    public static int getMonthByMills(long time){
        if(time > 0){
            String[] ymd = DateUtil.getCurrentTime_YMD(time).split("-");
            String ret = ymd[1];
            return Integer.parseInt(ret);
        }
        return  1;
    }

    /**
     * 根据毫秒数获取日
     * @param time
     * @return
     */
    public static int getDayByMills(long time){
        if(time > 0){
            String[] ymd = DateUtil.getCurrentTime_YMD(time).split("-");
            String ret = ymd[2];
            return Integer.parseInt(ret);
        }
        return  1;
    }

    /**
     * 根据毫秒数获取周几
     * @param time
     * @return
     */
    public static String getWeekOfDate(long time) {

        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 根据年 月 获取对应的月份 天数
     * */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public static long getMonthStartTimeMills(long time){
        String sYear = ""+ getYearByMills(time);
        int month = getMonthByMills(time);
        String sMonth;
        if(month<10){
            sMonth = "0"+month;
        }else{
            sMonth = ""+month;
        }
        long startTime = DateUtil.getFormatDataMills(sYear+"-"+sMonth+"-01");
        return startTime;
    }

    public static long getMonthEndTimeMills(long time){
        int year = getYearByMills(time);
        String sYear = ""+ year;
        int month = getMonthByMills(time);
        String sMonth;
        if(month<10){
            sMonth = "0"+month;
        }else{
            sMonth = ""+month;
        }
        int day = getDaysByYearMonth(year,month);
        long endTime = DateUtil.getFormatDataMills(sYear+"-"+sMonth+"-"+day);
        return endTime;
    }



    public static String getDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date(timestamp * 1000));
    }

    /**
     * 时分秒显示
     *
     * @param time
     * @return
     */
    public static String formatMiss(int time) {
        String hh = time / 3600 > 9 ? time / 3600 + "" : "0" + time / 3600;
        String mm = (time % 3600) / 60 > 9 ? (time % 3600) / 60 + "" : "0" + (time % 3600) / 60;
        String ss = (time % 3600) % 60 > 9 ? (time % 3600) % 60 + "" : "0" + (time % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    /* 将字符串转为时间戳 */
    public static String getTimeToStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                Locale.CHINA);
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(Calendar.getInstance().getTimeInMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tmptime = String.valueOf(date.getTime()).substring(0, 10);

        return tmptime;
    }

    /**
     * 通过Calendar得到当前星期几的index
     *
     * @return
     */
    public static int getCurrentWeek_index(){
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 通过Calendar得到当前星期几
     *
     * @return
     */
    public static String getCurrent_week(){
       String mWay = String.valueOf(getCurrentWeek_index());
        if("1".equals(mWay)){
            mWay ="日";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mWay;
    }

    public static long[] getCurrentWeek()
    {
        int index = getCurrentWeek_index();
        long current = getCurrentTimeStamp();
        long start =0;
        long end = 0;
        if(index==1)
        {
            start = current -6*TIME_IN_MILLS_DAY;
            end = current;
        }
        else
        {
            start =current -(index-2)*TIME_IN_MILLS_DAY;
            end = current + (8-index)*TIME_IN_MILLS_DAY;
        }

        long[] startTime =new long[]{start,end,end+TIME_IN_MILLS_DAY};
        return startTime;
    }


    public static String getYMD(long time)
    {
        return YMD.format(time);
    }


    public static long getCurrentTimeStamp()
    {
        Date date = new Date();
        try {
            date =YMD.parse(YMD.format(Calendar.getInstance().getTimeInMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long getTimeStamp(String time)
    {
        Date date = new Date();
        try {
            date =YMD.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    /**
     * 通过年月得到天数
     *
     * @return
     */
    public static int getDaysForMonth(int month, int year) {

        if (month == 2) {

            boolean is29 = false;
            if((year%4==0)&&(year%100!=0)||(year%400==0))
            {
                is29 = true;
            }

            return is29 ? 29 : 28;
        }

        if (month == 4 || month == 6|| month == 9 || month == 11)
            return 30;
        else
            return 31;
    }

    /**
     * 通过年得到第一年第一天
     *
     * @return
     */

    public static String getStartDayForYear(int year) {
       String result = String.valueOf(year);
        return (result+"-"+"01-01");
    }

    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        if(formatType == null){
            formatType =  "yyyy-MM-dd";
        }
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }


    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        if(formatType == null){
            formatType =  "yyyy-MM-dd";
        }
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        if(formatType == null){
            formatType =  "yyyy-MM-dd";
        }
        return new SimpleDateFormat(formatType).format(data);
    }


    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        if(formatType == null){
            formatType =  "yyyy-MM-dd";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        if(TextUtils.isEmpty(formatType)){
            formatType =  "yyyy-MM-dd";
        }
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static Date parseTime(String time) {

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


//    public static String format(Date date) {
//        DateTime now = new DateTime();
//        DateTime today_start = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);
//        DateTime today_end = today_start.plusDays(1);
//        DateTime yesterday_start = today_start.minusDays(1);
//
//        if(date.after(today_start.toDate()) && date.before(today_end.toDate())) {
//            return String.format("今天 %s", new DateTime(date).toString("HH:mm"));
//        } else if(date.after(yesterday_start.toDate()) && date.before(today_start.toDate())) {
//            return String.format("昨天 %s", new DateTime(date).toString("HH:mm"));
//        }
//
//        return new DateTime(date).toString("yyyy-MM-dd HH:mm");
//    }

    public static String isShowTime(long time){

        try {

            long current = System.currentTimeMillis();
            int curentYeay = Integer.parseInt(longToString(current, "yyyy"));
            int currentMonth = Integer.parseInt(longToString(current, "MM"));
            int currentDay = Integer.parseInt(longToString(current, "dd"));


            int year = Integer.parseInt(longToString(time, "yyyy"));
            int month = Integer.parseInt(longToString(time, "MM"));
            int day = Integer.parseInt(longToString(time, "dd"));

            if(year != curentYeay){
                return longToString(time,"yyyy-MM-dd");
            }else {

                if(month != currentMonth){
                    return longToString(time,"MM-dd");
                }else {
                    if(day != currentDay){
                        return longToString(time,"MM-dd");
                    }else {
                        return longToString(time,"HH:mm");
                    }
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;

    }
}