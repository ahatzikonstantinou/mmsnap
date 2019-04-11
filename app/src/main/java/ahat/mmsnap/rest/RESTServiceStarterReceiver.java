package ahat.mmsnap.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RESTServiceStarterReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive( Context context, Intent intent )
    {
        RESTAlarmReceiver.setupAlarm( context );
    }
}
