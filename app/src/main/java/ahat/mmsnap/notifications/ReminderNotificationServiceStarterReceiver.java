package ahat.mmsnap.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;

import ahat.mmsnap.json.ActionPlansStorage;
import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.JSONArrayConverterActionPlan;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.CopingPlan;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.models.IfThenPlan.WeekDay;
import ahat.mmsnap.models.Reminder;

public class ReminderNotificationServiceStarterReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        try
        {
            startReminderAlarms( context );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Could not start reminder notification service after boot. Error: " + e.getMessage() );
        }
    }

    public static void startReminderAlarms( Context context ) throws IOException, JSONException, ConversionException
    {
        ActionPlansStorage aps = new ActionPlansStorage( context );
        JSONArrayConverterActionPlan jacap = new JSONArrayConverterActionPlan();
        aps.read( jacap );

        CopingPlansStorage cps = new CopingPlansStorage( context );
        JSONArrayConverterCopingPlan jaccp = new JSONArrayConverterCopingPlan();
        cps.read( jaccp );

        for( ActionPlan p : jacap.getActionPlans() )
        {
            startAlarm( context, p );
        }
        for( CopingPlan p : jaccp.getCopingPlans() )
        {
            startAlarm( context, p );
        }
    }

    private static void startAlarm( Context context, IfThenPlan plan )
    {
        if( !plan.active )
        {
            return;
        }

        Calendar now = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, plan.year );
        c.set( Calendar.WEEK_OF_YEAR, plan.weekOfYear );
        for( WeekDay day : plan.days )
        {
            c.set( Calendar.DAY_OF_WEEK, day.toCalendarDayOfWeek() );
            for( Reminder reminder : plan.reminders )
            {
                c.set( Calendar.HOUR, reminder.hour );
                c.set( Calendar.MINUTE, reminder.minute );

                if( c.after( now ) )
                {
                    ReminderAlarmReceiver.setupAlarm( context, plan.year, plan.weekOfYear, day, reminder.hour, reminder.minute, true );
                }
            }
        }
    }
}
