package ahat.mmsnap.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterActionPlan;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.MainActivity;
import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;
import ahat.mmsnap.Models.IfThenPlan.WeekDay;
import ahat.mmsnap.Models.Reminder;

public class ReminderNotificationServiceStarterReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        try
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
        catch( Exception e )
        {
            e.printStackTrace();
            Log.e( "MMSNAP:", "Could not start reminder notification service after boot. Error: " + e.getMessage() );
        }
    }

    private void startAlarm( Context context, IfThenPlan plan )
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
                    ReminderAlarmReceiver.setupAlarm( context, plan.year, plan.weekOfYear, day, reminder.hour, reminder.minute, ( plan instanceof ActionPlan ? "action" : "coping" ) );
                }
            }
        }
    }
}
