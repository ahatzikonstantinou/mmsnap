package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import ahat.mmsnap.ApplicationStatus;

public abstract class IfThenPlan implements Serializable, Cloneable
{
    public static final Comparator<IfThenPlan> comparator = new Comparator<IfThenPlan>()
    {
        @Override
        public int compare( IfThenPlan p1, IfThenPlan p2 )
        {
            return ( p1.year - p2.year )*52 + ( p1.weekOfYear - p2.weekOfYear );
        }
    };

    public IfThenPlan(){}

    public IfThenPlan( int id, String ifStatement, String thenStatement, Boolean active, int year, int weekOfYear,
//                       ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<Day> days )
                       ArrayList<ApplicationStatus.Behavior> targetBehaviors, ArrayList<WeekDay> days )
    {
        this.id = id;
        this.ifStatement = ifStatement;
        this.thenStatement = thenStatement;
        this.active = active;
        this.targetBehaviors = targetBehaviors;
        this.days = new ArrayList<>( days );
        this.year = year;
        this.weekOfYear = weekOfYear;
    }

    public boolean dayHasPassed( WeekDay day )
    {
        Calendar now = Calendar.getInstance();
        now.setTime( new Date() );
        now.set( Calendar.MINUTE, 0 );
        now.set( Calendar.HOUR, 0 );
        now.set( Calendar.SECOND, 0 );
        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, year );
        c.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        c.set( Calendar.MINUTE, 0 );
        c.set( Calendar.HOUR, 0 );
        c.set( Calendar.SECOND, 0 );
        switch( day )
        {
            case MONDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                break;
            case TUESDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
                break;
            case WEDNESDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
                break;
            case THURSDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
                break;
            case FRIDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
                break;
            case SATURDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                break;
            case SUNDAY:
                c.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
                break;
        }

        return now.after( c );
    }

    public abstract IfThenPlan createCopyInCurrentWeek( int newId );

    public boolean hasDaysAfter( WeekDay weekDay )
    {
//        for( Day day : days )
        for( WeekDay day : days )
        {
//            if( day.weekDay.ordinal() > weekDay.ordinal() )
            if( day.ordinal() > weekDay.ordinal() )
            {
                return true;
            }
        }
        return false;
    }

    public enum WeekDay
    {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

        public int toCalendarDayOfWeek()
        {
            return toCalendarDayOfWeek( this );
        }

        public static int toCalendarDayOfWeek( WeekDay day )
        {
            switch( day )
            {
                case MONDAY:
                    return Calendar.MONDAY;
                case TUESDAY:
                    return Calendar.TUESDAY;
                case WEDNESDAY:
                    return Calendar.WEDNESDAY;
                case THURSDAY:
                    return Calendar.THURSDAY;
                case FRIDAY:
                    return Calendar.FRIDAY;
                case SATURDAY:
                    return Calendar.SATURDAY;
                case SUNDAY:
                    return Calendar.SUNDAY;
            }
            return -1;
        }
    }

//    public class Day implements Serializable
//    {
//        private WeekDay weekDay;
//        private boolean evaluated;
//        private boolean successful;
//
//        public boolean isEvaluated()
//        {
//            return evaluated;
//        }
//        public void setEvaluated( boolean evaluated )
//        {
//            this.evaluated = evaluated;
//        }
//
//        public boolean isSuccessful()
//        {
//            return successful;
//        }
//        public void setSuccessful( boolean successful )
//        {
//            this.successful = successful;
//        }
//
//        public void evaluate( boolean successfull )
//        {
//            evaluated = true;
//            this.successful = successfull;
//        }
//
//        public WeekDay getWeekDay()
//        {
//            return weekDay;
//        }
//
//        public Day( WeekDay weekDay )
//        {
//            boolean evaluated  = false;
//            boolean successful = false;
//            this.weekDay = weekDay;
//        }
//    }

    public int id = -1;
    public String ifStatement = "";
    public String thenStatement = "";
    public Boolean active = false;
    public ArrayList<ApplicationStatus.Behavior> targetBehaviors = new ArrayList<>();
//    public ArrayList<Day> days = new ArrayList<>();
    public ArrayList<WeekDay> days = new ArrayList<>();
    public int year = 0;
    public int weekOfYear = 0;

    public boolean isTarget( ApplicationStatus.Behavior behavior )
    {
        return targetBehaviors.contains( behavior );
    }

    public void clearDays()
    {
        days.clear();
    }

//    public void addDay( WeekDay weekDay )
//    {
//        days.add( new Day( weekDay ) );
//    }
    public void addDay( WeekDay weekDay ) { days.add( weekDay ); }

//    public boolean hasDay( WeekDay weekDay ) { return null != getDay( weekDay ); }
    public boolean hasDay( WeekDay weekDay ) { return days.contains( weekDay ); }

//    private Day getDay( WeekDay weekDay )
//    {
//        for( int i = 0 ; i < days.size() ; i++ )
//        {
//            Day d = days.get( i );
//            if( d.weekDay == weekDay )
//            {
//                return d;
//            }
//        }
//
//        return null;
//    }
//
//    public boolean isEvaluated()
//    {
//        for( int i = 0; i < days.size(); i++ )
//        {
//            Day d = days.get( i );
//            if( !d.isEvaluated() )
//            {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    public boolean isEvaluated( WeekDay weekDay )
//    {
//        Day d = getDay( weekDay );
//        if( null != d )
//        {
//            return d.isEvaluated();
//        }
//
//        return false;
//    }
//
//    public boolean isSuccessful( WeekDay weekDay )
//    {
//        Day d = getDay( weekDay );
//        if( null != d )
//        {
//            return d.isSuccessful();
//        }
//
//        return false;
//    }
//
//    public boolean evaluate( WeekDay weekDay, boolean success )
//    {
//        Day d = getDay( weekDay );
//        if( null != d )
//        {
//            d.evaluate( success );
//            return true;
//        }
//
//        return false;
//    }
//
//    public boolean needsEvaluation()
//    {
//        Calendar now = Calendar.getInstance();
//        Calendar ic = Calendar.getInstance();
//        ic.set( Calendar.YEAR, year );
//        ic.set( Calendar.WEEK_OF_YEAR, weekOfYear );
//        for( int i = 0 ; i < days.size() ; i++ )
//        {
//            Day d = days.get( i );
//            switch( d.weekDay )
//            {
//                case MONDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
//                    break;
//                case TUESDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
//                    break;
//                case WEDNESDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
//                    break;
//                case THURSDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
//                    break;
//                case FRIDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
//                    break;
//                case SATURDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
//                    break;
//                case SUNDAY:
//                    ic.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
//                    break;
//            }
//
//            if( now.after( ic ) && !d.isEvaluated() )
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public void resetEvaluations()
//    {
//        for( int i = 0 ; i < days.size() ; i++ )
//        {
//            Day d = days.get( i );
//            d.setEvaluated( false );
//            d.setSuccessful( false );
//        }
//    }
}
