package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.ArrayList;

import ahat.mmsnap.ApplicationStatus;

public class CopingPlan extends IfThenPlan implements Serializable
{
    public CopingPlan( int id, String ifStatement, String thenStatement, Boolean active, int year, int weekOfYear,
                       ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<Day> days )
    {
        super( id, ifStatement, thenStatement, active, year, weekOfYear, targetBehaviors, days );
    }

    public CopingPlan( IfThenPlan plan )
    {
        super( plan.id, plan.ifStatement, plan.thenStatement, plan.active, plan.year, plan.weekOfYear, plan.targetBehaviors, plan.days );
    }

    public CopingPlan()
    {
        super();
    }

    public static CopingPlan createNew()
    {
        CopingPlan plan = new CopingPlan();
        plan.id = -1;
        return plan;
    }
}
