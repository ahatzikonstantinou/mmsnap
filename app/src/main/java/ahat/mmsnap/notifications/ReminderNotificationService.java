package ahat.mmsnap.notifications;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;

import ahat.mmsnap.json.ActionPlansStorage;
import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.JSONArrayConverterActionPlan;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.MainActivity;
import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.models.IfThenPlan.WeekDay;
import ahat.mmsnap.R;

public class ReminderNotificationService extends IntentService
{
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public ReminderNotificationService()
    {
        super( ReminderNotificationService.class.getSimpleName() );
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        initChannels( this );
        return Service.START_STICKY;
    }

    public static Intent createIntentStart( Context context, int year, int weekOfYear, WeekDay day, int hour, int minute )
    {
        Intent intent = new Intent( context, ReminderNotificationService.class );
        intent.setAction( ACTION_START );
        intent.putExtra( "year", year );
        intent.putExtra( "weekOfYear", weekOfYear );
        intent.putExtra( "weekDay", day.name() );
        intent.putExtra( "hour", hour );
        intent.putExtra( "minute", minute );
        return intent;
    }

    public static Intent createIntentDelete( Context context )
    {
        Intent intent = new Intent( context, ReminderNotificationService.class );
        intent.setAction( ACTION_DELETE );
        return intent;
    }

    @Override
    protected void onHandleIntent( @Nullable Intent intent )
    {
        try
        {
            String action = intent.getAction();
            if( ACTION_START.equals( action ) )
            {
                processStartNotification( intent );
            }
            if( ACTION_DELETE.equals( action ) )
            {
                processDeleteNotification( intent );
            }
        }
        finally
        {
            WakefulBroadcastReceiver.completeWakefulIntent( intent );
        }
    }

    private void processDeleteNotification( Intent intent )
    {
        Log.d( "Ahat: ", "ReminderNotificationService.processDeleteNotification was called" );
    }

    public void initChannels( Context context )
    {
        if ( Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
        NotificationChannel channel = new NotificationChannel( "mmsnap",
                                                               "MMSNAP Channel",
                                                               NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription( "MMSNAP Channel" );
        notificationManager.createNotificationChannel( channel );
    }

    private void processStartNotification( Intent intent )
    {
        try
        {
            int year = intent.getIntExtra( "year", 0 );
            int weekOfYear = intent.getIntExtra( "weekOfYear", 0 );
            WeekDay day = WeekDay.valueOf( intent.getStringExtra( "weekDay" ) );
            int hour = intent.getIntExtra( "hour", 0 );
            int minute = intent.getIntExtra( "minute", 0 );

            ActionPlansStorage aps = new ActionPlansStorage( this );
            JSONArrayConverterActionPlan jacap = new JSONArrayConverterActionPlan();
            aps.read( jacap );
            sendNotifications( jacap.getActionPlans(), year, weekOfYear, day, hour, minute );

            CopingPlansStorage cps = new CopingPlansStorage( this );
            JSONArrayConverterCopingPlan jaccp = new JSONArrayConverterCopingPlan();
            cps.read( jaccp );
            sendNotifications( jaccp.getCopingPlans(), year, weekOfYear, day, hour, minute );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Failed generating notification for plan reminder. Error: " + e.getMessage() );
        }
    }

    private void sendNotifications( ArrayList<? extends IfThenPlan> plans, int year, int weekOfYear, IfThenPlan.WeekDay day, int hour, int minute )
    {
            IfThenPlan plan = null;
            for( IfThenPlan p : plans )
            {
                if( p.active && p.year == year && p.weekOfYear == weekOfYear && p.hasDay( day ) && p.hasReminder( hour, minute ) )
                {
                    plan = p;
                }
            }

            if( null == plan )
            {
                // plan not found, has been deleted, plan reminder has been deleted, or pplan has been set inactive
                return;
            }


            String message = "IF " + plan.ifStatement + " THEN " + plan.thenStatement + ".";
            if( plan instanceof ActionPlan )
            {
                message += " Also, IF " + ( (ActionPlan) plan ).copingIfStatement + " THEN " + ( (ActionPlan) plan ).copingThenStatement + ".";

            }

                final NotificationCompat.Builder builder = new NotificationCompat.Builder( this, "mmsnap" );
            builder.setContentTitle( "Plan Reminder" )
                   .setAutoCancel( true )
                   .setColor( getResources().getColor( R.color.colorAccent ) )
                   .setContentText( message )
                   .setSmallIcon( R.drawable.if_then_section_logo );

            PendingIntent pendingIntent = PendingIntent.getActivity( this,
                                                                     NOTIFICATION_ID,
                                                                     new Intent( this, MainActivity.class ),
                                                                     PendingIntent.FLAG_UPDATE_CURRENT );
            builder.setContentIntent( pendingIntent );
            builder.setDeleteIntent( PendingEvaluationsAlarmReceiver.getDeleteIntent( this ) );

            final NotificationManager manager = (NotificationManager) this.getSystemService( Context.NOTIFICATION_SERVICE );
            manager.notify( NOTIFICATION_ID, builder.build() );

    }
}
