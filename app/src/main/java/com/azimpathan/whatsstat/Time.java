package com.azimpathan.whatsstat;

import android.app.Application;
import android.content.Context;

/**
 * Created by AZIM_2 on 28/12/2017.
 */

/*
    An application class for getting the time as 'TIME ago'
*/

public class Time extends Application {

    private static final int SECONDS_MILIS=1000;    //1 second=1000 miliseconds
    private static final int MINUTES_MILIS=60*SECONDS_MILIS;
    private static final int HOURS_MILIS=60*MINUTES_MILIS;
    private static final int DAYS_MILIS=24*HOURS_MILIS;

    public static String getTimeAgo(long time)
    {
        //If timestamp is given in seconds, convert it into miliseconds
        if(time < 1000000000L)
            time*=1000;
        long current_time=System.currentTimeMillis();   //Getting current system time in miliseconds
        if(time <=0 ||time > current_time)
            return null;
        final long diffrence=current_time-time;
        if(diffrence < MINUTES_MILIS)
            return "few seconds ago";
        else if(diffrence < 2*MINUTES_MILIS)
            return "a minute ago";
        else if(diffrence < 50*MINUTES_MILIS)
            return diffrence/MINUTES_MILIS+" minutes ago";
        else if(diffrence < 90*MINUTES_MILIS)
            return "an hour ago";
        else if(diffrence < 24*HOURS_MILIS)
            return diffrence/HOURS_MILIS+" hours ago";
        else if(diffrence < 48*HOURS_MILIS)
            return "yesterday";
        else
            return diffrence/DAYS_MILIS+ " days ago";
    }
}
