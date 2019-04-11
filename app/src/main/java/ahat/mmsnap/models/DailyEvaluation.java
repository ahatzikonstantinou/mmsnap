package ahat.mmsnap.models;

import java.io.Serializable;
import java.util.ArrayList;

public class DailyEvaluation implements Serializable
{
    public  int                id;
    public  IfThenPlan         plan;
    private IfThenPlan.WeekDay weekDay;
    private boolean            evaluated;
    private boolean            successful;

    public DailyEvaluation( int id, IfThenPlan plan, IfThenPlan.WeekDay weekDay )
    {
        this.id = id;
        evaluated  = false;
        successful = false;
        this.weekDay = weekDay;
        this.plan = plan;
    }

    public DailyEvaluation( int id, IfThenPlan plan, IfThenPlan.WeekDay weekDay, boolean evaluated, boolean successful )
    {
        this.id = id;
        this.evaluated  = evaluated;
        this.successful = successful;
        this.weekDay = weekDay;
        this.plan = plan;
    }

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

    public void evaluate( boolean successful )
    {
        evaluated = true;
        this.successful = successful;
    }

    public IfThenPlan.WeekDay getWeekDay()
    {
        return weekDay;
    }

    public boolean dayHasPassed() { return plan.dayHasPassed( weekDay ); }

    /*
     * Returns an arraylist with a daily evaluation for every active plan that has a day that has passed.
     */
    public static ArrayList<DailyEvaluation> createMissing( ArrayList<DailyEvaluation> existing, ArrayList<IfThenPlan> plans )
    {
        ArrayList<DailyEvaluation> evaluations = new ArrayList<>();
        for( IfThenPlan plan : plans )
        {
            for( IfThenPlan.WeekDay day : plan.days )
            {
                if( !plan.dayHasPassed( day ) )
                {
                    continue;
                }

                boolean missing = true;
                for( DailyEvaluation evaluation : existing )
                {
                    if( evaluation.plan.id == plan.id && evaluation.weekDay == day && evaluation.plan.getClass().equals( plan.getClass() ) )
                    {
                        missing = false;
                        evaluations.add( evaluation );
                        break;
                    }
                }

                if( missing )
                {
                    evaluations.add( new DailyEvaluation( existing.size() + evaluations.size(), plan, day ) );
                }
            }
        }

        reIndex( evaluations );
        return evaluations;
    }

    private static void reIndex( ArrayList<DailyEvaluation> evaluations )
    {
        for( int i = 0; i < evaluations.size(); i++ )
        {
            evaluations.get( i ).id = i;
        }
    }

    public static boolean pendingExist( ArrayList<DailyEvaluation> evaluations )
    {
        for( int i  = 0 ; i < evaluations.size() ; i++ )
        {
            if( !evaluations.get( i ).isEvaluated() )
            {
                return true;
            }
        }

        return false;
    }
}
