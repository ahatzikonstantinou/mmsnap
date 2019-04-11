package ahat.mmsnap.rest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

public class RESTAlarmReceiver extends WakefulBroadcastReceiver
{

    private static final String ACTION_START_REST_SERVICE = "ACTION_START_REST_SERVICE";

    public static void setupAlarm( Context context )
    {
        // set the alarm every 30 minutes
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( System.currentTimeMillis() );
        calendar.set( Calendar.HOUR_OF_DAY, 8 );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent alarmIntent = getStartPendingIntent( context );
        alarmManager.setInexactRepeating( AlarmManager.RTC_WAKEUP,
                                          calendar.getTimeInMillis(),
                                          AlarmManager.INTERVAL_HALF_HOUR,
                                          alarmIntent );
    }

    @Override
    public void onReceive( Context context, Intent intent )
    {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if( ACTION_START_REST_SERVICE.equals( action ) )
        {
            Log.i( getClass().getSimpleName(), "onReceive from alarm, starting REST service");
            serviceIntent = RESTService.createIntentStart( context );
        }

        if( serviceIntent != null )
        {
            startWakefulService( context, serviceIntent );
        }
    }

    private static PendingIntent getStartPendingIntent( Context context )
    {
        Intent intent = new Intent( context, RESTAlarmReceiver.class );
        intent.setAction( ACTION_START_REST_SERVICE );
        return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
}
