package ahat.mmsnap.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

public class PendingEvaluationsAlarmReceiver extends WakefulBroadcastReceiver
{

        private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
        private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
        private static final int NOTIFICATIONS_INTERVAL_IN_HOURS = 2;

        public static void setupAlarm( Context context )
        {
            // set the alarm at 8 o'clock every day
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis( System.currentTimeMillis() );
            calendar.set( Calendar.HOUR_OF_DAY, 8 );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
            PendingIntent alarmIntent = getStartPendingIntent( context );
            alarmManager.setInexactRepeating( AlarmManager.RTC_WAKEUP,
                                              calendar.getTimeInMillis(),
                                              AlarmManager.INTERVAL_DAY,
                                              alarmIntent );
        }

        @Override
        public void onReceive( Context context, Intent intent )
        {
            String action = intent.getAction();
            Intent serviceIntent = null;
            if( ACTION_START_NOTIFICATION_SERVICE.equals( action ) )
            {
                Log.i( getClass().getSimpleName(), "onReceive from alarm, starting notification service");
                serviceIntent = PendingEvaluationNotificationService.createIntentStart( context );
            }
            else if( ACTION_DELETE_NOTIFICATION.equals( action ) )
            {
                Log.i( getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete" );
                serviceIntent = PendingEvaluationNotificationService.createIntentDelete( context );
            }

            if( serviceIntent != null )
            {
                startWakefulService( context, serviceIntent );
            }
        }



        private static PendingIntent getStartPendingIntent( Context context )
        {
            Intent intent = new Intent( context, PendingEvaluationsAlarmReceiver.class );
            intent.setAction( ACTION_START_NOTIFICATION_SERVICE );
            return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        }

        public static PendingIntent getDeleteIntent( Context context )
        {
            Intent intent = new Intent( context, PendingEvaluationsAlarmReceiver.class );
            intent.setAction( ACTION_DELETE_NOTIFICATION );
            return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        }
    
}
