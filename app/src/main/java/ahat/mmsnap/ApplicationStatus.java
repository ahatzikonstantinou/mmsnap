package ahat.mmsnap;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Like a configuration class, stores data regarding application state, start date, etc
 */
public class ApplicationStatus
{
    public boolean weeklyEvaluationPending()
    {
        // TODO
        return false;
    }

    enum Assessment { ILLNESS_PERCEPTION, HEALTH_RISK, SELF_EFFICACY, INTENTIONS, SELF_RATED_HEALTH };

    enum Behavior { EATING, ACTIVITY, ALCOHOL, SMOKING }

    enum State
    {
        NOT_LOGGED_IN,              // the user has not performed the initial login
        NO_INITIAL_EVALUATIONS,     // the user has not submitted the initial evaluations
        IN_ORDER,                   // everything is in order, the user has no pending issues
//        WEEKLY_EVALUATION_PENDING,  // the user has not submitted a weekly evaluation
//        DAILY_EVALUATION_PENDING,   // the user has not submitted a daily evaluation
        NO_FINAL_EVALUATIONS,       // the user has not submitted the final evaluations
        FINISHED                    // the duration of the program has finished and the user has submitted the final evaluations
    }

    public class SelfEfficacy
    {
        public boolean lifestyle = false;   // I am confident that I can adjust my life to a healthier lifestyle
        public boolean weekly_goals = false;   // I am confident that I can complete at least four health behaviour goals per week
        public boolean multimorbidity = false;   // I am confident that I can complete as many behaviour goals as necessary in order to manage my Multimorbidity
    }

    public Date startDate;
    public State state;
    public ArrayList<Behavior> problematicBehaviors = new ArrayList<>( 4 );
    public ArrayList<Assessment> initialAssessments= new ArrayList<>( Assessment.values().length );
    public ArrayList<Assessment> finalAssessments = new ArrayList<>( Assessment.values().length );

    public int eqvas;
    public SelfEfficacy selfEfficacy;

    private static final String FILENAME = "application_status.json";

    private ApplicationStatus()
    {
        state = State.NOT_LOGGED_IN;
        startDate = new Date();
        selfEfficacy = new SelfEfficacy();
    }

    public static ApplicationStatus loadApplicationStatus( Context context ) throws IOException, JSONException
    {
        ApplicationStatus as = new ApplicationStatus();

        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if( !file.exists() )
        {
            as.saveApplicationStatus( context );
            return as;
        }

        FileInputStream is = new FileInputStream( file );

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try
        {
            Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
            int n;
            while( ( n = reader.read( buffer ) ) != -1 )
            {
                writer.write( buffer, 0, n );
            }

            String jsonString = writer.toString();
            JSONObject jsonState = new JSONObject( jsonString  );

            as.state = State.valueOf( jsonState.getString( "state" ) );
            String[] dateParts =  jsonState.getString( "start_date" ).split( "-" );

            final Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, Integer.parseInt( dateParts[0] ) );
            cal.set( Calendar.MONTH, Integer.parseInt( dateParts[1] ) );
            cal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( dateParts[2] ) );
            cal.set( Calendar.HOUR_OF_DAY, 0 );
            cal.set( Calendar.MINUTE, 0 );
            cal.set( Calendar.SECOND, 0 );
            cal.set( Calendar.MILLISECOND, 0 );
            as.startDate = cal.getTime();

            JSONArray jsonBehaviors = jsonState.getJSONArray( "behaviors" );
            as.problematicBehaviors = new ArrayList<>( Behavior.values().length );
            for( int i = 0 ; i < jsonBehaviors.length() ; i++ )
            {
                as.problematicBehaviors.add( Behavior.valueOf( jsonBehaviors.getString( i ) ) );
            }

            JSONArray ia  = jsonState.getJSONArray( "initial_assessments" );
            as.initialAssessments = new ArrayList<>( Assessment.values().length );
            for( int i = 0 ; i < ia.length() ; i++ )
            {
                as.initialAssessments.add( Assessment.valueOf( ia.getString( i ) ) );
            }

            JSONArray fa  = jsonState.getJSONArray( "final_assessments" );
            as.finalAssessments = new ArrayList<>( Assessment.values().length );
            for( int i = 0 ; i < fa.length() ; i++ )
            {
                as.finalAssessments.add( Assessment.valueOf( fa.getString( i ) ) );
            }

            as.eqvas = jsonState.getInt( "eqvas" );
            as.selfEfficacy.multimorbidity = jsonState.getBoolean( "selfEfficacy.multimorbidity" );
            as.selfEfficacy.lifestyle = jsonState.getBoolean( "selfEfficacy.lifestyle" );
            as.selfEfficacy.weekly_goals = jsonState.getBoolean( "selfEfficacy.weekly_goals" );
        }
        finally
        {
            is.close();
        }
        return as;
    }

    public void saveApplicationStatus( Context context ) throws IOException, JSONException
    {
        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
        JSONObject o = new JSONObject();

        startDate = new Date(); // the help program starts NOW that all initial assessments are submitted
        Calendar cal = Calendar.getInstance();
        cal.setTime( startDate );
        o.put( "start_date", cal.get( Calendar.YEAR ) + "-" + cal.get( Calendar.MONTH ) + "-" + cal.get( Calendar.DAY_OF_MONTH ) );

        JSONArray b = new JSONArray();
        for( int i = 0 ; i < problematicBehaviors.size() ; i++ )
        {
            b.put( problematicBehaviors.get(i).name() );
        }
        o.put( "behaviors", b );



        JSONArray ia = new JSONArray();
        for( int i = 0 ; i < initialAssessments.size() ; i++ )
        {
            ia.put( initialAssessments.get(i).name() );
        }
        o.put( "initial_assessments", ia );

        if( State.NO_INITIAL_EVALUATIONS == state )
        {
            boolean allInitialAssessmentsSubmitted = true;

            for( Assessment a : Assessment.values() )
            {
                if( !initialAssessments.contains( a ) )
                {
                    allInitialAssessmentsSubmitted = false;
                    break;
                }
            }
            if( allInitialAssessmentsSubmitted )
            {
                state = State.IN_ORDER;
                startDate = new Date(); // the help program starts NOW that all initial assessments are submitted
                cal = Calendar.getInstance();
                cal.setTime( startDate );
                o.put( "start_date", cal.get( Calendar.YEAR ) + "-" + cal.get( Calendar.MONTH ) + "-" + cal.get( Calendar.DAY_OF_MONTH ) );
            }
        }


        JSONArray fa = new JSONArray();
        for( int i = 0 ; i < finalAssessments.size() ; i++ )
        {
            fa.put( finalAssessments.get(i).name() );
        }
        o.put( "final_assessments", fa );

        if( State.NO_FINAL_EVALUATIONS == state )
        {
            boolean allInitialAssessmentsSubmitted = true;

            for( Assessment a : Assessment.values() )
            {
                if( !initialAssessments.contains( a ) )
                {
                    allInitialAssessmentsSubmitted = false;
                    break;
                }
            }
            if( allInitialAssessmentsSubmitted )
            {
                state = State.FINISHED;
            }
        }

        o.put( "state", state.name() );


        o.put( "eqvas", eqvas );
        o.put( "selfEfficacy.multimorbidity", selfEfficacy.multimorbidity );
        o.put( "selfEfficacy.lifestyle", selfEfficacy.lifestyle );
        o.put( "selfEfficacy.weekly_goals", selfEfficacy.weekly_goals );


        try
        {
            fos.write( o.toString().getBytes() );
        }
        finally
        {
            fos.close();
        }
    }

}
