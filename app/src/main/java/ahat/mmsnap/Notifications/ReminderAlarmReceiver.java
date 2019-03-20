package ahat.mmsnap.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import ahat.mmsnap.ActionPlansDetailActivity;
import ahat.mmsnap.Models.IfThenPlan;
import ahat.mmsnap.Models.Reminder;

public class ReminderAlarmReceiver extends WakefulBroadcastReceiver
{

    private static final String ACTION_START_REMINDER_SERVICE = "ACTION_START_REMINDER_SERVICE";
    private static final String ACTION_DELETE_REMINDER_NOTIFICATION = "ACTION_DELETE_REMINDER_NOTIFICATION";

    private static int generateRequestCode( int year, int weekOfYear, IfThenPlan.WeekDay day, int hour, int minute )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        calendar.set( Calendar.DAY_OF_WEEK, day.toCalendarDayOfWeek() );
        calendar.set( Calendar.HOUR, hour );
        calendar.set( Calendar.MINUTE, minute );

        return (int) ( calendar.getTimeInMillis() / ( 1000 * 60 ) ); // time in minutes resolution. Good until 4000 AD when it becomes > Max Int

    }

    public static void setupAlarm( Context context, int year, int weekOfYear, IfThenPlan.WeekDay day, int hour, int minute, String planType )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        calendar.set( Calendar.DAY_OF_WEEK, day.toCalendarDayOfWeek() );
        calendar.set( Calendar.HOUR_OF_DAY, hour );
        calendar.set( Calendar.MINUTE, minute );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent alarmIntent = getStartPendingIntent( context, year, weekOfYear, day, hour, minute, planType );
        alarmManager.set( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent );
    }

    public static void cancelAlarms( Context context, int year, int weekOfYear, IfThenPlan.WeekDay day, int hour, int minute, String planType )
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.cancel( getStartPendingIntent( context, year, weekOfYear, day, hour, minute, planType ) );
    }


    @Override
    public void onReceive( Context context, Intent intent )
    {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if( action.startsWith( ACTION_START_REMINDER_SERVICE ) )
        {
            Log.i( getClass().getSimpleName(), "onReceive from alarm, starting reminder service");
            String[] parts = action.split( "_" );
            serviceIntent = ReminderNotificationService.createIntentStart( context,
                                                                           Integer.parseInt( parts[4] ),
                                                                           Integer.parseInt( parts[5] ),
                                                                           IfThenPlan.WeekDay.valueOf( parts[6] ),
                                                                           Integer.parseInt( parts[7] ),
                                                                           Integer.parseInt( parts[8] ),
                                                                           parts[9]
                                                                           );
        }
        else if( ACTION_DELETE_REMINDER_NOTIFICATION.equals( action ) )
        {
            Log.i( getClass().getSimpleName(), "onReceive delete notification action, starting reminder service to handle delete" );
            serviceIntent = ReminderNotificationService.createIntentDelete( context );
        }

        if( serviceIntent != null )
        {
            startWakefulService( context, serviceIntent );
        }
    }

    private static PendingIntent getStartPendingIntent( Context context,  int year, int weekOfYear, IfThenPlan.WeekDay day, int hour, int minute, String planType )
    {
        Intent intent = new Intent( context, ReminderAlarmReceiver.class );
        String combinedAction = ACTION_START_REMINDER_SERVICE +
                                "_" + String.valueOf( year ) +
                                "_" + String.valueOf( weekOfYear ) +
                                "_" + day.name() +
                                "_" + String.valueOf( hour ) +
                                "_" + String.valueOf( minute ) +
                                "_" + planType;
        intent.setAction( combinedAction );
        return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    public static PendingIntent getDeleteIntent( Context context )
    {
        Intent intent = new Intent( context, ReminderAlarmReceiver.class );
        intent.setAction( ACTION_DELETE_REMINDER_NOTIFICATION );
        return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }


}
