package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import ahat.mmsnap.ApplicationStatus;

public class CopingPlan extends IfThenPlan implements Serializable, Cloneable
{
    public CopingPlan( int id, String ifStatement, String thenStatement, Boolean active, int year, int weekOfYear,
                       ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<WeekDay> days, ArrayList<Reminder> reminders )
    {
        super( id, ifStatement, thenStatement, active, year, weekOfYear, targetBehaviors, days, reminders );
    }

    public CopingPlan( IfThenPlan plan )
    {
        super( plan.id, plan.ifStatement, plan.thenStatement, plan.active, plan.year, plan.weekOfYear, plan.targetBehaviors, plan.days, plan.reminders );
    }

    public CopingPlan()
    {
        super();
    }

    public static CopingPlan createNew()
    {
        CopingPlan plan = new CopingPlan();
        plan.id = -1;
        Calendar c = Calendar.getInstance();
        plan.year = c.get( Calendar.YEAR );
        plan.weekOfYear = c.get( Calendar.WEEK_OF_YEAR );

        return plan;
    }

    public CopingPlan createCopyInCurrentWeek( int newId )
    {
        CopingPlan plan = new CopingPlan( this );
        plan.id = newId;
        Calendar c = Calendar.getInstance();
        plan.year = c.get( Calendar.YEAR );
        plan.weekOfYear = c.get( Calendar.WEEK_OF_YEAR );
        plan.days.clear();
        plan.reminders.clear();
        plan.active = false;
        return plan;

    }
}
