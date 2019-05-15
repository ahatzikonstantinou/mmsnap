package ahat.mmsnap.notifications;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ahat.mmsnap.ApplicationStatus;
import ahat.mmsnap.MainActivity;
import ahat.mmsnap.R;

public class PendingEvaluationNotificationService extends IntentService
{
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public PendingEvaluationNotificationService()
    {
        super( PendingEvaluationNotificationService.class.getSimpleName() );
    }

    @Override
    public void onCreate()
    {
        initChannels( this );
    }

    public static Intent createIntentStart( Context context )
    {
        Intent intent = new Intent( context, PendingEvaluationNotificationService.class );
        intent.setAction( ACTION_START );
        return intent;
    }

    public static Intent createIntentDelete( Context context )
    {
        Intent intent = new Intent( context, PendingEvaluationNotificationService.class );
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
                processStartNotification();
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
        Log.d( "Ahat: ", "PendingEvaluationNotificationService.processDeleteNotification was called" );
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

    private void processStartNotification()
    {
        try
        {
            String message = "";
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            if( as.pendingDailyEvaluationsExist() )
            {
                message += "daily";
            }
            if( as.pendingWeeklyEvaluationsExist() )
            {
                message += ( message.length() > 0 ? " and " : "" ) + "weekly";
            }

            if( 0 == message.length() )
            {
                return;
            }

            message = "There exist pending " + message + " evaluations. Please complete them so that MMSNAP can assess your progress.";

                final NotificationCompat.Builder builder = new NotificationCompat.Builder( this, "mmsnap" );
            builder.setContentTitle( "Pending Evaluations" )
                   .setAutoCancel( true )
                   .setColor( getResources().getColor( R.color.colorAccent ) )
                   .setContentText( message )
                   .setSmallIcon( R.drawable.mmsnap_section_logo );

            PendingIntent pendingIntent = PendingIntent.getActivity( this,
                                                                     NOTIFICATION_ID,
                                                                     new Intent( this, MainActivity.class ),
                                                                     PendingIntent.FLAG_UPDATE_CURRENT );
            builder.setContentIntent( pendingIntent );
            builder.setDeleteIntent( PendingEvaluationsAlarmReceiver.getDeleteIntent( this ) );

            final NotificationManager manager = (NotificationManager) this.getSystemService( Context.NOTIFICATION_SERVICE );
            manager.notify( NOTIFICATION_ID, builder.build() );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Failed generating notification for pending evaluations. Error: " + e.getMessage() );
        }
    }
}
