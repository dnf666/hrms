package com.facishare.crm.customeraccount.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

    public static long getYesterdayBenginTime() {
        DateTime now = DateTime.now();
        DateTime yest = now.minusDays(1);
        return yest.dayOfWeek().roundFloorCopy().getMillis();
    }

    public static long getNowBenginTime() {
        DateTime now = DateTime.now();
        return now.dayOfWeek().roundFloorCopy().getMillis();
    }

    public static DateTime getNowBenginDateTime() {
        DateTime now = DateTime.now();
        return now.dayOfWeek().roundFloorCopy();
    }

    public static long getYesterdayEndTime() {
        DateTime now = DateTime.now();
        DateTime yest = now.dayOfWeek().roundFloorCopy().minusSeconds(1);
        System.out.println(yest);
        return yest.getMillis();
    }

    public static long getMillisecondsOfDayStart(long createTime) {
        Date date = new Date(createTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = simpleDateFormat.format(date);
        long startTime = 0;
        try {
            startTime = simpleDateFormat.parse(strDate).getTime();
        } catch (ParseException e) {
            log.info("ParseException of Date", e.getMessage());
        }
        return startTime;

    }

    public static Date getTomorrowDate(Date date) {
        long timeStamp = date.getTime();
        DateTime dateTime = new DateTime(timeStamp);
        DateTime afterTommorow = dateTime.plusDays(1);
        return new Date(afterTommorow.getMillis());
    }
}
