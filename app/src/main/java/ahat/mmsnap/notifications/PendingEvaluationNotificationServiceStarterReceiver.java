package ahat.mmsnap.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PendingEvaluationNotificationServiceStarterReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        PendingEvaluationsAlarmReceiver.setupAlarm( context );
    }
}
