package ahat.mmsnap.Models;

import android.drm.DrmStore;

import java.io.Serializable;
import java.util.ArrayList;

import ahat.mmsnap.ApplicationStatus;

public class ActionPlan extends IfThenPlan implements Serializable
{
    public String copingIfStatement = "";
    public String copingThenStatement = "";

    public ActionPlan( int id, String ifStatement, String thenStatement, String copingIf, String copingThen, boolean active, int year, int weekOfYear, ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<Day> days )
    {
        super( id, ifStatement, thenStatement, active, year, weekOfYear, targetBehaviors, days );
        copingIfStatement = copingIf;
        copingThenStatement = copingThen;
    }

    public ActionPlan( IfThenPlan plan, String copingIf, String copingThen )
    {
        super( plan.id, plan.ifStatement, plan.thenStatement, plan.active, plan.year, plan.weekOfYear, plan.targetBehaviors, plan.days );
        copingIfStatement = copingIf;
        copingThenStatement = copingThen;
    }

    public ActionPlan()
    {
        super();
    }

    public static ActionPlan createNew()
    {
        ActionPlan plan = new ActionPlan();
        plan.id = -1;
        return plan;

    }
}