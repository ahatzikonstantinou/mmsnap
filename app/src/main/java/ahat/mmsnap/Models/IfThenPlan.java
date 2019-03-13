package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.ArrayList;

import ahat.mmsnap.ApplicationStatus;

public abstract class IfThenPlan implements Serializable, Cloneable
{
    public IfThenPlan(){}

    public IfThenPlan( int id, String ifStatement, String thenStatement, Boolean active, int year, int weekOfYear,
                       ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<Day> days )
    {
        this.id = id;
        this.ifStatement = ifStatement;
        this.thenStatement = thenStatement;
        this.active = active;
        this.targetBehaviors = targetBehaviors;
        this.days = days;
        this.year = year;
        this.weekOfYear = weekOfYear;
    }

    public enum Day { MONDAY, TUESDAY, THURSDAY, WEDNESDAY, FRIDAY, SATURDAY, SUNDAY;
        private boolean evaluated  = false;
        private boolean successful = false;

        public boolean isEvaluated()
        {
            return evaluated;
        }
        public void setEvaluated( boolean evaluated )
        {
            this.evaluated = evaluated;
        }

        public boolean isSuccessful()
        {
            return successful;
        }
        public void setSuccessful( boolean successful )
        {
            this.successful = successful;
        }

        public void evaluate( boolean successfull )
        {
            evaluated = true;
            this.successful = successfull;
        }
    };
    public int id = -1;
    public String ifStatement = "";
    public String thenStatement = "";
    public Boolean active = false;
    public ArrayList<ApplicationStatus.Behavior> targetBehaviors = new ArrayList<>();
    public ArrayList<Day> days = new ArrayList<>();
    public int year = 0;
    public int weekOfYear = 0;

    public boolean pendingExist()
    {
        for( int i = 0 ; i < days.size() ; i++ )
        {
            if( !days.get( i ).isEvaluated() )
            {
                return false;
            }
        }

        return true;
    }

    public boolean isTarget( ApplicationStatus.Behavior behavior )
    {
        return targetBehaviors.contains( behavior );
    }

    public boolean isEvaluated()
    {
        for( int i = 0; i < days.size(); i++ )
        {
            Day d = days.get( i );
            if( !d.isEvaluated() )
            {
                return false;
            }
        }

        return true;
    }

    public boolean isEvaluated( Day day )
    {
        if( days.contains( day ) )
        {
            for( int i = 0; i < days.size(); i++ )
            {
                Day d = days.get( i );
                if( d == day )
                {
                    return d.isEvaluated();
                }
            }
        }

        return false;
    }

    public boolean isSuccessful( Day day )
    {
        if( days.contains( day ) )
        {
            for( int i = 0; i < days.size(); i++ )
            {
                Day d = days.get( i );
                if( d == day )
                {
                    return d.isSuccessful();
                }
            }
        }

        return false;
    }

    public boolean evaluate( Day day, boolean success )
    {
        for( int i = 0 ; i < days.size() ; i++ )
        {
            Day d = days.get( i );
            if( d == day )
            {
                d.evaluate( success );
                return true;
            }
        }

        return false;
    }
}
