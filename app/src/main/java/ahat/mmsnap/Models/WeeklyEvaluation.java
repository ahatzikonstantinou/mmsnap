package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyEvaluation implements Serializable
{
    private int weekOfYear;
    private int year;

    public int getWeekOfYear()
    {
        return weekOfYear;
    }

    public int getYear()
    {
        return year;
    }

    public Date start()
    {
        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, year );
        c.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        c.get( c.getFirstDayOfWeek() );

        return c.getTime();
    }

    public Date end()
    {
        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, year );
        c.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        c.get( c.getFirstDayOfWeek() + 7 );

        return c.getTime();
    }

    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 7;

    private int diet;
    private int smoking;
    private int physicalActivity;
    private int alcohol;

    public void score( int diet, int physicalActivity, int alcohol, int smoking ) throws Exception
    {
        if( targetDiet && ( diet > MAX_SCORE || diet < MIN_SCORE ) )
        {
            throw new Exception( "Illegal diet score " + String.valueOf( diet ) + ". Valid range is [" + String.valueOf( MIN_SCORE ) + ".." + String.valueOf( MAX_SCORE ) + "]" );
        }
        if( targetSmoking && ( smoking > MAX_SCORE || smoking < MIN_SCORE ) )
        {
            throw new Exception( "Illegal smoking score " + String.valueOf( diet ) + ". Valid range is [" + String.valueOf( MIN_SCORE ) + ".." + String.valueOf( MAX_SCORE ) + "]" );
        }
        if( targetPhysicalActivity && ( physicalActivity > MAX_SCORE || physicalActivity < MIN_SCORE ) )
        {
            throw new Exception( "Illegal physical activity score " + String.valueOf( diet ) + ". Valid range is [" + String.valueOf( MIN_SCORE ) + ".." + String.valueOf( MAX_SCORE ) + "]" );
        }
        if( targetAlcohol && ( alcohol > MAX_SCORE || alcohol < MIN_SCORE ) )
        {
            throw new Exception( "Illegal alcohol drinking score " + String.valueOf( diet ) + ". Valid range is [" + String.valueOf( MIN_SCORE ) + ".." + String.valueOf( MAX_SCORE ) + "]" );
        }
        this.diet = diet;
        this.smoking = smoking;
        this.physicalActivity = physicalActivity;
        this.alcohol = alcohol;
    }

    public int getDiet()
    {
        return diet;
    }

    public int getSmoking()
    {
        return smoking;
    }

    public int getPhysicalActivity()
    {
        return physicalActivity;
    }

    public int getAlcohol()
    {
        return alcohol;
    }

    // Fields storing whether a particular behavior is a target and therefore should be included in the weekly evaluation
    public final boolean targetDiet;
    public final boolean targetSmoking;
    public final boolean targetPhysicalActivity;
    public final boolean targetAlcohol;


    public WeeklyEvaluation( int year, int weekOfYear, boolean targetDiet, boolean targetSmoking, boolean targetPhysicalActivity, boolean targetAlcohol )
    {
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.targetDiet = targetDiet;
        this.targetSmoking = targetSmoking;
        this.targetPhysicalActivity = targetPhysicalActivity;
        this.targetAlcohol = targetAlcohol;
        diet = -1;
        smoking = -1;
        physicalActivity = -1;
        alcohol = -1;
    }

    public WeeklyEvaluation( int year, int weekOfYear, boolean targetDiet, boolean targetSmoking, boolean targetPhysicalActivity, boolean targetAlcohol, int diet, int smoking, int physicalActivity, int alcohol )
    {
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.targetDiet = targetDiet;
        this.targetSmoking = targetSmoking;
        this.targetPhysicalActivity = targetPhysicalActivity;
        this.targetAlcohol = targetAlcohol;
        this.diet = diet;
        this.smoking = smoking;
        this.physicalActivity = physicalActivity;
        this.alcohol = alcohol;
    }

    public boolean isScored()
    {
        return  ( !targetDiet || ( diet >= MIN_SCORE && diet <= MAX_SCORE ) ) &&
                ( !targetSmoking || ( smoking >= MIN_SCORE && smoking <= MAX_SCORE ) ) &&
                ( !targetPhysicalActivity || ( physicalActivity >= MIN_SCORE && physicalActivity <= MAX_SCORE ) ) &&
                ( !targetAlcohol || ( alcohol >= MIN_SCORE && alcohol <= MAX_SCORE ) );
    }

    public boolean dietIsScored() { return  targetDiet && diet >= MIN_SCORE && diet <= MAX_SCORE; }
    public boolean smokingIsScored() { return  targetSmoking && smoking >= MIN_SCORE && smoking <= MAX_SCORE; }
    public boolean physicalActivityIsScored() { return targetPhysicalActivity && physicalActivity >= MIN_SCORE && physicalActivity <= MAX_SCORE; }
    public boolean alcoholIsScored() { return  targetAlcohol && alcohol >= MIN_SCORE && alcohol <= MAX_SCORE; }



    /*
     * Returns an arraylist with a weekly evaluation for every week between startDate and endDate, including the existing ones.
     */
    public static ArrayList<WeeklyEvaluation> createMissing( Date startDate, Date endDate, ArrayList<WeeklyEvaluation> existing, boolean targetDiet, boolean targetSmoking, boolean targetPhysicalActivity, boolean targetAlcohol )
    {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime( endDate );

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, start.get( Calendar.YEAR ) );
        c.set( Calendar.WEEK_OF_YEAR, start.get( Calendar.WEEK_OF_YEAR ) );
        c.set( Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() );   // go to a monday in this week
        c.add( Calendar.DATE, 7 );

        ArrayList<WeeklyEvaluation> evaluations = new ArrayList<>();
        while( end.after( c ) )
        {
            int year = c.get( Calendar.YEAR );
            int weekOfYear = c.get( Calendar.WEEK_OF_YEAR );

            boolean exists = false;
            for( int i = 0 ; i < existing.size() ; i++ )
            {
                WeeklyEvaluation e = existing.get( i );
                if( e.weekOfYear == weekOfYear && e.year == year )
                {
                    exists = true;
                    evaluations.add( e );
                    break;
                }
            }
            if( !exists )
            {
                evaluations.add( new WeeklyEvaluation( year, weekOfYear, targetDiet, targetSmoking, targetPhysicalActivity, targetAlcohol ) );
            }
            c.add( Calendar.DATE, 7 );
        }

        return evaluations;
    }

    public static boolean pendingExist( ArrayList<WeeklyEvaluation> evaluations )
    {
        for( int i  = 0 ; i < evaluations.size() ; i++ )
        {
            if( !evaluations.get( i ).isScored() )
            {
                return true;
            }
        }

        return false;
    }

}
