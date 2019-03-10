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

    int DurationDays = 40;

    public boolean weeklyEvaluationPending()
    {
        // TODO
        return false;
    }
    private boolean dailyEvaluationPending()
    {
        // TODO
        return false;
    }

//    enum State
//    {
//        NOT_LOGGED_IN,              // the user has not performed the initial login
//        NO_INITIAL_EVALUATIONS,     // the user has not submitted the initial evaluations
//        IN_ORDER,                   // everything is in order, the user has no pending issues
//        //        WEEKLY_EVALUATION_PENDING,  // the user has not submitted a weekly evaluation
////        DAILY_EVALUATION_PENDING,   // the user has not submitted a daily evaluation
//        NO_FINAL_EVALUATIONS,       // the user has not submitted the final evaluations
//        FINISHED                    // the duration of the program has finished and the user has submitted the final evaluations
//    }

    enum Assessment { ILLNESS_PERCEPTION, HEALTH_RISK, SELF_EFFICACY, INTENTIONS, SELF_RATED_HEALTH }

    enum Behavior { EATING, ACTIVITY, ALCOHOL, SMOKING }

    public class SelfEfficacy
    {
        public boolean lifestyle = false;   // I am confident that I can adjust my life to a healthier lifestyle
        public boolean weekly_goals = false;   // I am confident that I can complete at least four health behaviour goals per week
        public boolean multimorbidity = false;   // I am confident that I can complete as many behaviour goals as necessary in order to manage my Multimorbidity
    }

    private Context context;
    private Date startDate;
    public Date getStartDate() { return startDate; }
    public State getState() { return state; }
    public ArrayList<Behavior> problematicBehaviors = new ArrayList<>( 4 );
    private ArrayList<Assessment> initialAssessments= new ArrayList<>( Assessment.values().length );
    private ArrayList<Assessment> finalAssessments = new ArrayList<>( Assessment.values().length );
    public void addAssessment( Assessment assessment ) throws IOException, JSONException
    {
        addAssessment( assessment, true );
    }
    public void addAssessment( Assessment assessment, boolean save ) throws IOException, JSONException
    {
        ArrayList<Assessment> assessments = null;
        State previous = getState();
        if( NoInitialAssessments.NAME == previous.name() )
        {
            assessments = initialAssessments;
        }
        else if( NoFinalAssessments.NAME == previous.name()  )
        {
            assessments = finalAssessments;
        }
        if( null == assessments )
        {
            return;
        }

        if( !assessments.contains( assessment ) )
        {
            assessments.add( assessment );
        }

        state.moveNext();

        if( save )
        {
            saveApplicationStatus();
        }
    }
    public boolean initialAssessmentsContain( Assessment assessment )
    {
        return initialAssessments.contains( assessment );
    }
    public boolean finalAssessmentsContain( Assessment assessment )
    {
        return finalAssessments.contains( assessment );
    }

    public void userLoggedIn() throws IOException, JSONException
    {
        if( state.moveNext() )
        {
            saveApplicationStatus();
        }
    }

    public int eqvas;
    public SelfEfficacy selfEfficacy;

    private static final String FILENAME = "application_status.json";

    private ApplicationStatus( Context context ) throws Exception
    {
        this.context = context;
        startDate = new Date();
        selfEfficacy = new SelfEfficacy();
        StateFactory f = new StateFactory( this );
        state = f.create( NotLoggedIn.NAME );
    }

    private ApplicationStatus( Context context, String stateNAME ) throws Exception
    {
        this.context = context;
        startDate = new Date();
        selfEfficacy = new SelfEfficacy();
        StateFactory f = new StateFactory( this );
        this.state = f.create( stateNAME );
    }

    public static ApplicationStatus loadApplicationStatus( Context context ) throws Exception
    {
        ApplicationStatus as = new ApplicationStatus( context );

        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if( !file.exists() )
        {
            as.saveApplicationStatus();
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

            as = new ApplicationStatus( context, jsonState.getString( "state" ) );

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

    public void saveApplicationStatus() throws IOException, JSONException
    {
        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
        JSONObject o = new JSONObject();

        startDate = new Date();
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


        JSONArray fa = new JSONArray();
        for( int i = 0 ; i < finalAssessments.size() ; i++ )
        {
            fa.put( finalAssessments.get(i).name() );
        }
        o.put( "final_assessments", fa );

        o.put( "state", getState().name() );

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

    State state;
    public void setState( State state ) { this.state = state ;}

    public class StateFactory
    {
        private ApplicationStatus applicationStatus;

        public StateFactory( ApplicationStatus applicationStatus ){this.applicationStatus = applicationStatus;}

        public State create( String stateNAME ) throws Exception
        {
            switch( stateNAME )
            {
                case NotLoggedIn.NAME:
                    return new NotLoggedIn( this.applicationStatus );
                case NoInitialAssessments.NAME:
                    return new NoInitialAssessments( applicationStatus );
                case InOrder.NAME:
                    return new InOrder( applicationStatus );
                case NoFinalAssessments.NAME:
                    return new NoFinalAssessments( applicationStatus );
                case Finished.NAME:
                    return new Finished( applicationStatus );
                default:
                    throw new Exception( "Unknown Application state " + stateNAME );
            }
        }
    }

    public abstract class State
    {
        protected ApplicationStatus applicationStatus;
        State( ApplicationStatus applicationStatus )
        {
            this.applicationStatus = applicationStatus;
        }

        public abstract  boolean moveNext();
        public abstract String name();
    }

    public class NotLoggedIn extends State
    {
        static final String NAME = "NotLoggedIn";
        public String name() { return NAME; }
        NotLoggedIn( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext() { applicationStatus.setState( new NoInitialAssessments( applicationStatus ) ); return true; }
    }

    public abstract class NoAssessments extends State
    {
        NoAssessments( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        protected boolean allAssessmentsSubmitted( ArrayList<ApplicationStatus.Assessment> assessments )
        {
            boolean allAssessmentsSubmitted = true;

            for( ApplicationStatus.Assessment a : ApplicationStatus.Assessment.values() )
            {
                if( !assessments.contains( a ) )
                {
                    allAssessmentsSubmitted = false;
                    break;
                }
            }

            return allAssessmentsSubmitted;
        }
    }
    public class NoInitialAssessments extends NoAssessments
    {
        static final String NAME = "NoInitialAssessments";
        public String name() { return NAME; }
        NoInitialAssessments( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            if( allAssessmentsSubmitted( applicationStatus.initialAssessments ) )
            {
                applicationStatus.setState( new InOrder( applicationStatus ) );
                applicationStatus.startDate = new Date(); // the research program starts when all initial evaluations are submitted
                return true;
            }
            return false;
        }
    }
    public class InOrder extends State
    {
        static final String NAME = "InOrder";
        public String name() { return NAME; }
        InOrder( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            if( weeklyEvaluationPending() )
            {
                applicationStatus.setState( new WeeklyEvaluationPending( applicationStatus ) );
                return true;
            }
            else if( dailyEvaluationPending() )
            {
                applicationStatus.setState( new DailyEvaluationPending( applicationStatus ) );
                return true;
            }
            else if( programDurationExpired() )
            {
                applicationStatus.setState( new NoFinalAssessments( applicationStatus ) );
                return true;
            }
            return false;
        }

        protected boolean programDurationExpired()
        {
            Calendar c = Calendar.getInstance();
            c.setTime( startDate );
            c.add( Calendar.DATE, DurationDays );
            return c.after( new Date() );
        }
    }
    public class WeeklyEvaluationPending extends InOrder
    {
        static final String NAME = "WeeklyEvaluationPending";
        public String name() { return NAME; }
        WeeklyEvaluationPending( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            if( weeklyEvaluationPending() )
            {
                return false;
            }
            else if( dailyEvaluationPending() )
            {
                applicationStatus.setState( new DailyEvaluationPending( applicationStatus ) );
                return true;
            }
            else if( programDurationExpired() )
            {
                applicationStatus.setState( new NoFinalAssessments( applicationStatus ) );
                return true;
            }

            applicationStatus.setState( new InOrder( applicationStatus ) );
            return true;
        }
    }
    public class DailyEvaluationPending extends InOrder
    {
        static final String NAME = "DaillyEvaluationPending";
        public String name() { return NAME; }
        DailyEvaluationPending( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            if( dailyEvaluationPending() )
            {
                return false;
            }
            else if( weeklyEvaluationPending() )
            {
                applicationStatus.setState( new WeeklyEvaluationPending( applicationStatus ) );
                return true;
            }
            else if( programDurationExpired() )
            {
                applicationStatus.setState( new NoFinalAssessments( applicationStatus ) );
                return true;
            }

            applicationStatus.setState( new InOrder( applicationStatus ) );
            return true;
        }
    }
    public class NoFinalAssessments extends NoAssessments
    {
        static final String NAME = "NoFinalAssessments";
        public String name() { return NAME; }
        NoFinalAssessments( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            if( allAssessmentsSubmitted( applicationStatus.finalAssessments ) )
            {
                applicationStatus.setState( new Finished( applicationStatus ) );
                return true;
            }
            return false;
        }
    }
    public class Finished extends State
    {
        static final String NAME = "Finished";
        public String name() { return NAME; }
        Finished( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext() { return false; }
    }


//    public class ApplicationStateMachine
//    {
//        private State             state;
//        private ApplicationStatus applicationStatus;
//
//        public State getState() { return state; }
//
//        public ApplicationStateMachine( State state, ApplicationStatus applicationStatus )
//        {
//            this.state = state;
//            this.applicationStatus = applicationStatus;
//        }
//
//        /*
//         * return true if the state has changed, false if the state remains the same
//         */
//        public boolean moveNext()
//        {
//
//            State previous = state;
//            switch( state )
//            {
//                case NOT_LOGGED_IN:
//                    state = State.NO_INITIAL_EVALUATIONS;
//                    break;
//                case NO_INITIAL_EVALUATIONS:
//                    if( allInitialAssessmentsSubmitted() )
//                    {
//                        state = State.IN_ORDER;
//                    }
//                    break;
//                case IN_ORDER:
//                    if( programDurationExpired() )
//                    {
//                        state = State.NO_FINAL_EVALUATIONS;
//                    }
//                    break;
//                case NO_FINAL_EVALUATIONS:
//                    if( allFinalEvaluationsSubmitted() )
//                    {
//                        state = State.FINISHED;
//                    }
//                    break;
//                case FINISHED:
//                    break;
//            }
//
//            return previous != state;
//        }
//
//        private boolean allFinalEvaluationsSubmitted()
//        {
//            return allAssessmentsSubmitted( finalAssessments );
//        }
//
//        private boolean programDurationExpired()
//        {
//            Calendar c = Calendar.getInstance();
//            c.setTime( startDate );
//            c.add( Calendar.DATE, DurationDays );
//            return c.after( new Date() );
//        }
//
//        private boolean allInitialAssessmentsSubmitted()
//        {
//            return allAssessmentsSubmitted( initialAssessments );
//        }
//
//
//    }

}
