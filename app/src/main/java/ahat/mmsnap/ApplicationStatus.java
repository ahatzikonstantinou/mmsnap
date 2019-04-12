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

import ahat.mmsnap.json.ActionPlansStorage;
import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.DailyEvaluationsStorage;
import ahat.mmsnap.json.JSONArrayConverterActionPlan;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.json.JSONArrayConverterDailyEvaluation;
import ahat.mmsnap.json.JSONArrayConverterWeeklyEvaluation;
import ahat.mmsnap.json.JSONConverterDailyEvaluation;
import ahat.mmsnap.json.JSONConverterWeeklyEvaluation;
import ahat.mmsnap.json.Util;
import ahat.mmsnap.json.WeeklyEvaluationsStorage;
import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.CopingPlan;
import ahat.mmsnap.models.CounterfactualThought;
import ahat.mmsnap.models.DailyEvaluation;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.models.WeeklyEvaluation;

/*
 * Like a configuration class, stores data regarding application state, start date, etc
 */
public class ApplicationStatus
{

    public static final int MIN_ACTIVE_PLANS_PER_WEEK = 5;
    public static final int DURATION_DAYS = 40;


    private Context context;
    private Date startDate;
    public Date getStartDate() { return startDate; }
    public void setStartDate( Date date ) { startDate = date; }
    public CounterfactualThought counterfactualThought;




    //
    // records of initial/final assessment submission (no data, just the act of submiting it)
    //
    enum Assessment { ILLNESS_PERCEPTION, HEALTH_RISK, SELF_EFFICACY, INTENTIONS, SELF_RATED_HEALTH }

    private ArrayList<Assessment> initialAssessments = new ArrayList<>( Assessment.values().length );
    private ArrayList<Assessment> finalAssessments = new ArrayList<>( Assessment.values().length );
    public void addAssessment( Assessment assessment ) throws IOException, JSONException, ConversionException
    {
        addAssessment( assessment, true );
    }
    public void addAssessment( Assessment assessment, boolean save ) throws IOException, JSONException, ConversionException
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
            save();
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
    public void clearInitialAssessments() { initialAssessments.clear(); }
    public void clearFinalAssessments() { finalAssessments.clear(); }



    public void userLoggedIn() throws IOException, JSONException, ConversionException
    {
        if( state.moveNext() )
        {
            save();
        }
    }




    //
    // assessment types and data
    ///
    public enum Behavior {DIET, ACTIVITY, ALCOHOL, SMOKING }

    public class SelfEfficacy
    {
        public boolean lifestyle = false;   // I am confident that I can adjust my life to a healthier lifestyle
        public boolean weekly_goals = false;   // I am confident that I can complete at least four health behaviour goals per week
        public boolean multimorbidity = false;   // I am confident that I can complete as many behaviour goals as necessary in order to manage my Multimorbidity
    }

    public class IntentionsAndPlans
    {
        public boolean plan_exercise_when; //when to exercise
        public boolean plan_exercise_pastweek; //In the past week have you exercised less than 150 min moderate or 60 min intense manner?
        public boolean plan_exercise_where; //where to exercise
        public boolean plan_exercise_how; //how to exercise
        public boolean plan_exercise_often; //how often to exercise
        public boolean plan_exercise_whom; //with whom to exercise
        public boolean plan_exercise_interfere; //what to do if something interferes with my plans
        public boolean plan_exercise_setbacks; //how to cope with possilble setbacks
        public boolean plan_exercise_situations; //what to do in difficult situations in order to act according to my intentions
        public boolean plan_exercise_opportunities; //which good opportunities for action to take
        public boolean plan_exercise_lapses; //when I have to pay extra attention to prevent lapses
        public boolean plan_intend_times; //exercise several times per week
        public boolean plan_intend_sweat; //work up a sweat regularly int.
        public boolean plan_intend_regularly; //exercise regularly
        public boolean plan_intend_active; //be physically active for a minimum of 30 minutes at least three times a week
        public boolean plan_intend_leisure; //increase my leisure time activity
        public boolean plan_intend_rehabilitation; //adhere to the exercise regime prescribed to me during the rehabilitation
    }

    public class SelfRatedHealth
    {
        public int one_condition_more_serious     = -1;
        public int time_spent_managing            = -1;
        public int feel_overwhelmed               = -1;
        public int causes_are_linked              = -1;
        public int difficult_all_medications      = -1;
        public int limited_activities             = -1;
        public int different_medications_problems = -1;
        public int mixing_medications             = -1;
        public int less_effective_treatments      = -1;
        public int one_cause_another              = -1;
        public int one_dominates                  = -1;
        public int conditions_interact            = -1;
        public int difficult_best_treatment       = -1;
        public int reduced_social_life            = -1;
        public int unhappy                        = -1;
        public int anxious                        = -1;
        public int angry                          = -1;
        public int sad                            = -1;
        public int irritable                      = -1;
        public int sad_struggle                   = -1;
    }

    public int eqvas;
    public void setEQVAS( int _eqvas ) { eqvas = _eqvas; serverData.add( eqvas ); }
    public SelfEfficacy selfEfficacy;
    public ArrayList<Behavior> problematicBehaviors = new ArrayList<>( 4 );
    public IntentionsAndPlans intentionsAndPlans;
    public SelfRatedHealth selfRatedHealth;


    //
    // server data
    //
    public enum Phase { INITIAL, FINAL };

    public class ServerTimestamp
    {
        Date  submissionDate      = null;
        Date  acknowledgementDate = null;
        Phase phase               = null;

        ServerTimestamp(){}
        ServerTimestamp( Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            this.submissionDate = submissionDate;
            this.acknowledgementDate = acknowledgementDate;
            this.phase = phase;
        }

        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = new JSONObject();
            json.put( "submissionDate", Util.toJsonString( submissionDate ) );
            json.put( "acknowledgementDate", Util.toJsonString( acknowledgementDate ) );
            json.put( "phase", null == phase ? "" : phase.name() );
            return json;
        }

        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            submissionDate = Util.dateFromJsonString( json.getString( "submissionDate" ) );
            acknowledgementDate = Util.dateFromJsonString( json.getString( "acknowledgementDate" ) );
            String ps = json.getString( "phase" );
            phase = 0 == ps.length() ? null : Phase.valueOf( ps );
        }
    }

    public class ServerEQVAS extends ServerTimestamp
    {
        public int eqvas;
        ServerEQVAS(){}
        ServerEQVAS( int eqvas, Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            super( submissionDate, acknowledgementDate, phase );
            this.eqvas = eqvas;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            json.put( "eqvas", eqvas );
            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            eqvas = json.getInt( "eqvas" );
        }
    }

    public class ServerProblematicBehaviors extends ServerTimestamp
    {
        public Boolean diet;
        public Boolean physicalActivity;
        public Boolean smoking;
        public Boolean alcohol;
        ServerProblematicBehaviors(){}
        ServerProblematicBehaviors( ArrayList<Behavior> problematicBehaviors, Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            super( submissionDate, acknowledgementDate, phase );
            this.diet = problematicBehaviors.contains( Behavior.DIET );
            this.physicalActivity = problematicBehaviors.contains( Behavior.ACTIVITY );
            this.smoking = problematicBehaviors.contains( Behavior.SMOKING );
            this.alcohol = problematicBehaviors.contains( Behavior.ALCOHOL );
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            json.put( "diet", diet );
            json.put( "physicalActivity", physicalActivity );
            json.put( "smoking", smoking );
            json.put( "alcohol", alcohol );
            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            diet = json.getBoolean( "diet" );
            physicalActivity = json.getBoolean( "physicalActivity" );
            smoking = json.getBoolean( "smoking" );
            alcohol = json.getBoolean( "alcohol" );
        }
    }

    public class ServerSelfEfficacy extends ServerTimestamp
    {
        public SelfEfficacy selfEfficacy;
        ServerSelfEfficacy(){}
        ServerSelfEfficacy( SelfEfficacy selfEfficacy, Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            super( submissionDate, acknowledgementDate, phase );
            this.selfEfficacy = selfEfficacy;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            json.put( "selfEfficacy.multimorbidity", selfEfficacy.multimorbidity );
            json.put( "selfEfficacy.lifestyle", selfEfficacy.lifestyle );
            json.put( "selfEfficacy.weekly_goals", selfEfficacy.weekly_goals );
            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            selfEfficacy.multimorbidity = json.getBoolean( "selfEfficacy.multimorbidity" );
            selfEfficacy.lifestyle = json.getBoolean( "selfEfficacy.lifestyle" );
            selfEfficacy.weekly_goals = json.getBoolean( "selfEfficacy.weekly_goals" );
        }
    }

    public class ServerIntentionsAndPlans extends ServerTimestamp
    {
        public IntentionsAndPlans intentionsAndPlans;
        ServerIntentionsAndPlans(){}
        ServerIntentionsAndPlans( IntentionsAndPlans intentionsAndPlans, Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            super( submissionDate, acknowledgementDate, phase );
            this.intentionsAndPlans = intentionsAndPlans;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            json.put( "intentionsAndPlans.plan_exercise_when", intentionsAndPlans.plan_exercise_when );
            json.put( "intentionsAndPlans.plan_exercise_pastweek", intentionsAndPlans.plan_exercise_pastweek );
            json.put( "intentionsAndPlans.plan_exercise_where", intentionsAndPlans.plan_exercise_where );
            json.put( "intentionsAndPlans.plan_exercise_how", intentionsAndPlans.plan_exercise_how );
            json.put( "intentionsAndPlans.plan_exercise_often", intentionsAndPlans.plan_exercise_often );
            json.put( "intentionsAndPlans.plan_exercise_whom", intentionsAndPlans.plan_exercise_whom );
            json.put( "intentionsAndPlans.plan_exercise_interfere", intentionsAndPlans.plan_exercise_interfere );
            json.put( "intentionsAndPlans.plan_exercise_setbacks", intentionsAndPlans.plan_exercise_setbacks );
            json.put( "intentionsAndPlans.plan_exercise_situations", intentionsAndPlans.plan_exercise_situations );
            json.put( "intentionsAndPlans.plan_exercise_opportunities", intentionsAndPlans.plan_exercise_opportunities );
            json.put( "intentionsAndPlans.plan_exercise_lapses", intentionsAndPlans.plan_exercise_lapses );
            json.put( "intentionsAndPlans.plan_intend_times", intentionsAndPlans.plan_intend_times );
            json.put( "intentionsAndPlans.plan_intend_sweat", intentionsAndPlans.plan_intend_sweat );
            json.put( "intentionsAndPlans.plan_intend_regularly", intentionsAndPlans.plan_intend_regularly );
            json.put( "intentionsAndPlans.plan_intend_active", intentionsAndPlans.plan_intend_active );
            json.put( "intentionsAndPlans.plan_intend_leisure", intentionsAndPlans.plan_intend_leisure );
            json.put( "intentionsAndPlans.plan_intend_rehabilitation", intentionsAndPlans.plan_intend_rehabilitation );

            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            intentionsAndPlans.plan_exercise_when = json.getBoolean( "intentionsAndPlans.plan_exercise_when" );
            intentionsAndPlans.plan_exercise_pastweek = json.getBoolean( "intentionsAndPlans.plan_exercise_pastweek" );
            intentionsAndPlans.plan_exercise_where = json.getBoolean( "intentionsAndPlans.plan_exercise_where" );
            intentionsAndPlans.plan_exercise_how = json.getBoolean( "intentionsAndPlans.plan_exercise_how" );
            intentionsAndPlans.plan_exercise_often = json.getBoolean( "intentionsAndPlans.plan_exercise_often" );
            intentionsAndPlans.plan_exercise_whom = json.getBoolean( "intentionsAndPlans.plan_exercise_whom" );
            intentionsAndPlans.plan_exercise_interfere = json.getBoolean( "intentionsAndPlans.plan_exercise_interfere" );
            intentionsAndPlans.plan_exercise_setbacks = json.getBoolean( "intentionsAndPlans.plan_exercise_setbacks" );
            intentionsAndPlans.plan_exercise_situations = json.getBoolean( "intentionsAndPlans.plan_exercise_situations" );
            intentionsAndPlans.plan_exercise_opportunities = json.getBoolean( "intentionsAndPlans.plan_exercise_opportunities" );
            intentionsAndPlans.plan_exercise_lapses = json.getBoolean( "intentionsAndPlans.plan_exercise_lapses" );
            intentionsAndPlans.plan_intend_times = json.getBoolean( "intentionsAndPlans.plan_intend_times" );
            intentionsAndPlans.plan_intend_sweat = json.getBoolean( "intentionsAndPlans.plan_intend_sweat" );
            intentionsAndPlans.plan_intend_regularly = json.getBoolean( "intentionsAndPlans.plan_intend_regularly" );
            intentionsAndPlans.plan_intend_active = json.getBoolean( "intentionsAndPlans.plan_intend_active" );
            intentionsAndPlans.plan_intend_leisure = json.getBoolean( "intentionsAndPlans.plan_intend_leisure" );
            intentionsAndPlans.plan_intend_rehabilitation = json.getBoolean( "intentionsAndPlans.plan_intend_rehabilitation" );
        }
    }

    public class ServerSelfRatedHealth extends ServerTimestamp
    {
        public SelfRatedHealth selfRatedHealth;
        ServerSelfRatedHealth(){}
        ServerSelfRatedHealth( SelfRatedHealth selfRatedHealth, Date submissionDate, Date acknowledgementDate, Phase phase )
        {
            super( submissionDate, acknowledgementDate, phase );
            this.selfRatedHealth = selfRatedHealth;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            json.put( "selfRatedHealth.one_condition_more_serious", selfRatedHealth.one_condition_more_serious );
            json.put( "selfRatedHealth.time_spent_managing", selfRatedHealth.time_spent_managing );
            json.put( "selfRatedHealth.feel_overwhelmed", selfRatedHealth.feel_overwhelmed );
            json.put( "selfRatedHealth.causes_are_linked", selfRatedHealth.causes_are_linked );
            json.put( "selfRatedHealth.difficult_all_medications", selfRatedHealth.difficult_all_medications );
            json.put( "selfRatedHealth.limited_activities", selfRatedHealth.limited_activities );
            json.put( "selfRatedHealth.different_medications_problems", selfRatedHealth.different_medications_problems );
            json.put( "selfRatedHealth.mixing_medications", selfRatedHealth.mixing_medications );
            json.put( "selfRatedHealth.less_effective_treatments", selfRatedHealth.less_effective_treatments );
            json.put( "selfRatedHealth.one_cause_another", selfRatedHealth.one_cause_another );
            json.put( "selfRatedHealth.one_dominates", selfRatedHealth.one_dominates );
            json.put( "selfRatedHealth.conditions_interact", selfRatedHealth.conditions_interact );
            json.put( "selfRatedHealth.difficult_best_treatment", selfRatedHealth.difficult_best_treatment );
            json.put( "selfRatedHealth.reduced_social_life", selfRatedHealth.reduced_social_life );
            json.put( "selfRatedHealth.unhappy", selfRatedHealth.unhappy );
            json.put( "selfRatedHealth.anxious", selfRatedHealth.anxious );
            json.put( "selfRatedHealth.angry", selfRatedHealth.angry );
            json.put( "selfRatedHealth.sad", selfRatedHealth.sad );
            json.put( "selfRatedHealth.irritable", selfRatedHealth.irritable );
            json.put( "selfRatedHealth.sad_struggle", selfRatedHealth.sad_struggle );

            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            selfRatedHealth.one_condition_more_serious = json.getInt( "selfRatedHealth.one_condition_more_serious" );
            selfRatedHealth.time_spent_managing = json.getInt( "selfRatedHealth.time_spent_managing" );
            selfRatedHealth.feel_overwhelmed = json.getInt( "selfRatedHealth.feel_overwhelmed" );
            selfRatedHealth.causes_are_linked = json.getInt( "selfRatedHealth.causes_are_linked" );
            selfRatedHealth.difficult_all_medications = json.getInt( "selfRatedHealth.difficult_all_medications" );
            selfRatedHealth.limited_activities = json.getInt( "selfRatedHealth.limited_activities" );
            selfRatedHealth.different_medications_problems = json.getInt( "selfRatedHealth.different_medications_problems" );
            selfRatedHealth.mixing_medications = json.getInt( "selfRatedHealth.mixing_medications" );
            selfRatedHealth.less_effective_treatments = json.getInt( "selfRatedHealth.less_effective_treatments" );
            selfRatedHealth.one_cause_another = json.getInt( "selfRatedHealth.one_cause_another" );
            selfRatedHealth.one_dominates = json.getInt( "selfRatedHealth.one_dominates" );
            selfRatedHealth.conditions_interact = json.getInt( "selfRatedHealth.conditions_interact" );
            selfRatedHealth.difficult_best_treatment = json.getInt( "selfRatedHealth.difficult_best_treatment" );
            selfRatedHealth.reduced_social_life = json.getInt( "selfRatedHealth.reduced_social_life" );
            selfRatedHealth.unhappy = json.getInt( "selfRatedHealth.unhappy" );
            selfRatedHealth.anxious = json.getInt( "selfRatedHealth.anxious" );
            selfRatedHealth.angry = json.getInt( "selfRatedHealth.angry" );
            selfRatedHealth.sad = json.getInt( "selfRatedHealth.sad" );
            selfRatedHealth.irritable = json.getInt( "selfRatedHealth.irritable" );
            selfRatedHealth.sad_struggle = json.getInt( "selfRatedHealth.sad_struggle" );
        }
    }

    public class ServerDailyEvaluation extends ServerTimestamp
    {
        public DailyEvaluation dailyEvaluation;
        ServerDailyEvaluation(){}
        ServerDailyEvaluation( DailyEvaluation dailyEvaluation, Date submissionDate, Date acknowledgementDate )
        {
            super( submissionDate, acknowledgementDate, null );
            this.dailyEvaluation = dailyEvaluation;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            JSONConverterDailyEvaluation jc = new JSONConverterDailyEvaluation( dailyEvaluation );
            jc.setJsonObject( json );
            jc.to();

            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            JSONConverterDailyEvaluation jc = new JSONConverterDailyEvaluation( json );
            jc.from();
            dailyEvaluation = jc.getDailyEvaluation();
        }
    }

    public class ServerWeeklyEvaluation extends ServerTimestamp
    {
        public WeeklyEvaluation weeklyEvaluation;
        ServerWeeklyEvaluation(){}
        ServerWeeklyEvaluation( WeeklyEvaluation weeklyEvaluation, Date submissionDate, Date acknowledgementDate )
        {
            super( submissionDate, acknowledgementDate, null );
            this.weeklyEvaluation = weeklyEvaluation;
        }

        @Override
        JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = super.toJson();
            JSONConverterWeeklyEvaluation jc = new JSONConverterWeeklyEvaluation( weeklyEvaluation );
            jc.setJsonObject( json );
            jc.to();

            return json;
        }

        @Override
        void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            super.fromJson( json );
            JSONConverterWeeklyEvaluation jc = new JSONConverterWeeklyEvaluation( json );
            jc.from();
            weeklyEvaluation = jc.getWeeklyEvaluation();
        }
    }

    public class ServerData
    {
        ArrayList<ServerEQVAS> eqvas;
        ArrayList<ServerProblematicBehaviors> problematicBehaviors;
        ServerSelfEfficacy selfEfficacyInitial;
        ServerSelfEfficacy selfEfficacyFinal;
        ServerIntentionsAndPlans intentionsAndPlansInitial;
        ServerIntentionsAndPlans intentionsAndPlansFinal;
        ServerSelfRatedHealth selfRatedHealthInitial;
        ServerSelfRatedHealth selfRatedHealthFinal;
        ArrayList<ServerDailyEvaluation> dailyEvaluations;
        ArrayList<ServerWeeklyEvaluation> weeklyEvaluations;

        public void add( final int eqvas )
        {
            this.eqvas.add( new ServerEQVAS( eqvas, new Date(), null, getPhase() ) );
        }
        public void add( final ArrayList<Behavior> problematicBehaviors )
        {
            this.problematicBehaviors.add( new ServerProblematicBehaviors( problematicBehaviors, new Date(), null, getPhase() ) );
        }
        public void add( final SelfRatedHealth selfRatedHealth )
        {
            if( getPhase() == Phase.INITIAL )
            {
                selfRatedHealthInitial = new ServerSelfRatedHealth( selfRatedHealth, new Date(), null, Phase.INITIAL );
            }
            else if( getPhase() == Phase.FINAL )
            {
                selfRatedHealthFinal = new ServerSelfRatedHealth( selfRatedHealth, new Date(), null, Phase.FINAL );
            }
        }
        public void add( final IntentionsAndPlans intentionsAndPlans )
        {
            if( getPhase() == Phase.INITIAL )
            {
                intentionsAndPlansInitial = new ServerIntentionsAndPlans( intentionsAndPlans, new Date(), null, Phase.INITIAL );
            }
            else if( getPhase() == Phase.FINAL )
            {
                intentionsAndPlansFinal = new ServerIntentionsAndPlans( intentionsAndPlans, new Date(), null, Phase.FINAL );
            }
        }
        public void add( final SelfEfficacy selfEfficacy )
        {
            if( getPhase() == Phase.INITIAL )
            {
                selfEfficacyInitial = new ServerSelfEfficacy( selfEfficacy, new Date(), null, Phase.INITIAL );
            }
            else if( getPhase() == Phase.FINAL )
            {
                selfEfficacyFinal = new ServerSelfEfficacy( selfEfficacy, new Date(), null, Phase.FINAL );
            }
        }
        public void add( final DailyEvaluation dailyEvaluation )
        {
            dailyEvaluations.add( new ServerDailyEvaluation( dailyEvaluation, new Date(), null ) );
        }
        public void add( final WeeklyEvaluation weeklyEvaluation )
        {
            weeklyEvaluations.add( new ServerWeeklyEvaluation( weeklyEvaluation, new Date(), null ) );
        }

        private Phase getPhase()
        {
            if( state.name().equals( NoInitialAssessments.NAME ) )
            {
                return Phase.INITIAL;
            }
            else if( state.name().equals( NoFinalAssessments.NAME ) )
            {
                return Phase.FINAL;
            }
            return null;
        }

        ServerData()
        {
            eqvas = new ArrayList<>();
            problematicBehaviors = new ArrayList<>();
            selfEfficacyInitial = new ServerSelfEfficacy( new SelfEfficacy(), null, null, null );
            selfEfficacyFinal = new ServerSelfEfficacy( new SelfEfficacy(), null, null, null );
            intentionsAndPlansInitial = new ServerIntentionsAndPlans( new IntentionsAndPlans(), null, null, null );
            intentionsAndPlansFinal = new ServerIntentionsAndPlans( new IntentionsAndPlans(), null, null, null );
            selfRatedHealthInitial = new ServerSelfRatedHealth( new SelfRatedHealth(), null, null, null );
            selfRatedHealthFinal = new ServerSelfRatedHealth( new SelfRatedHealth(), null, null, null );
            dailyEvaluations = new ArrayList<>();
            weeklyEvaluations = new ArrayList<>();
        }

        public JSONObject toJson() throws JSONException, ConversionException
        {
            JSONObject json = new JSONObject();

            JSONArray eqvasJsonArray = new JSONArray();
            for( ServerEQVAS value  : eqvas )
            {
                eqvasJsonArray.put( value.toJson() );
            }
            json.put( "eqvas", eqvasJsonArray );

            JSONArray behaviorsJsonArray = new JSONArray();
            for( ServerProblematicBehaviors value  : problematicBehaviors )
            {
                behaviorsJsonArray.put( value.toJson() );
            }
            json.put( "problematicBehaviors", behaviorsJsonArray );

            json.put( "selfEfficacyInitial", selfEfficacyInitial.toJson() );
            json.put( "selfEfficacyFinal", selfEfficacyFinal.toJson() );
            json.put( "intentionsAndPlansInitial", intentionsAndPlansInitial.toJson() );
            json.put( "intentionsAndPlansFinal", intentionsAndPlansFinal.toJson() );
            json.put( "selfRatedHealthInitial", selfRatedHealthInitial.toJson() );
            json.put( "selfRatedHealthFinal", selfRatedHealthFinal.toJson() );

            JSONArray dailyEvaluationsJsonArray = new JSONArray();
            for( ServerDailyEvaluation value  : dailyEvaluations )
            {
                dailyEvaluationsJsonArray.put( value.toJson() );
            }
            json.put( "dailyEvaluations", dailyEvaluationsJsonArray );

            JSONArray weeklyEvaluationsJsonArray = new JSONArray();
            for( ServerWeeklyEvaluation value  : weeklyEvaluations )
            {
                weeklyEvaluationsJsonArray.put( value.toJson() );
            }
            json.put( "weeklyEvaluations", weeklyEvaluationsJsonArray );


            return json;
        }

        public void fromJson( JSONObject json ) throws JSONException, ConversionException
        {
            JSONArray eqvasJsonArray = json.getJSONArray( "eqvas" );
            eqvas = new ArrayList<>( eqvasJsonArray.length() );
            for( int i  = 0 ; i < eqvasJsonArray.length() ; i++ )
            {
                ServerEQVAS s = new ServerEQVAS();
                s.fromJson( eqvasJsonArray.getJSONObject( i ) );
                eqvas.add( s );
            }

            JSONArray problematicBehaviorsJsonArray = json.getJSONArray( "problematicBehaviors" );
            problematicBehaviors = new ArrayList<>( problematicBehaviorsJsonArray.length() );
            for( int i  = 0 ; i < problematicBehaviorsJsonArray.length() ; i++ )
            {
                ServerProblematicBehaviors s = new ServerProblematicBehaviors();
                s.fromJson( problematicBehaviorsJsonArray.getJSONObject( i ) );
                problematicBehaviors.add( s );
            }

            selfEfficacyInitial.fromJson( json.getJSONObject( "selfEfficacyInitial" ) );
            selfEfficacyFinal.fromJson( json.getJSONObject( "selfEfficacyFinal" ) );
            intentionsAndPlansInitial.fromJson( json.getJSONObject( "intentionsAndPlansInitial" ) );
            intentionsAndPlansFinal.fromJson( json.getJSONObject( "intentionsAndPlansFinal" ) );
            selfRatedHealthInitial.fromJson( json.getJSONObject( "selfRatedHealthInitial" ) );
            selfRatedHealthFinal.fromJson( json.getJSONObject( "selfRatedHealthFinal" ) );

            JSONArray dailyEvaluationsJsonArray = json.getJSONArray( "dailyEvaluations" );
            dailyEvaluations = new ArrayList<>( dailyEvaluationsJsonArray.length() );
            for( int i  = 0 ; i < dailyEvaluationsJsonArray.length() ; i++ )
            {
                ServerDailyEvaluation s = new ServerDailyEvaluation();
                s.fromJson( dailyEvaluationsJsonArray.getJSONObject( i ) );
                dailyEvaluations.add( s );
            }

            JSONArray weeklyEvaluationsJsonArray = json.getJSONArray( "weeklyEvaluations" );
            weeklyEvaluations = new ArrayList<>( weeklyEvaluationsJsonArray.length() );
            for( int i  = 0 ; i < weeklyEvaluationsJsonArray.length() ; i++ )
            {
                ServerWeeklyEvaluation s = new ServerWeeklyEvaluation();
                s.fromJson( weeklyEvaluationsJsonArray.getJSONObject( i ) );
                weeklyEvaluations.add( s );
            }
        }
    }

    public ServerData serverData;




    //
    // constructors (singleton)
    //
    private ApplicationStatus( Context context ) throws Exception
    {
        this.context = context;
        startDate = new Date();
        selfEfficacy = new SelfEfficacy();
        intentionsAndPlans = new IntentionsAndPlans();
        selfRatedHealth = new SelfRatedHealth();
        StateFactory f = new StateFactory( this );
        state = f.create( NotLoggedIn.NAME );
        dailyEvaluations = new ArrayList<>();
        weeklyEvaluations = new ArrayList<>();
        counterfactualThought = new CounterfactualThought();
        serverData = new ServerData();
    }

    private ApplicationStatus( Context context, String stateNAME ) throws Exception
    {
        this.context = context;
        startDate = new Date();
        selfEfficacy = new SelfEfficacy();
        intentionsAndPlans = new IntentionsAndPlans();
        selfRatedHealth = new SelfRatedHealth();
        StateFactory f = new StateFactory( this );
        this.state = f.create( stateNAME );
        dailyEvaluations = new ArrayList<>();
        weeklyEvaluations = new ArrayList<>();
        counterfactualThought = new CounterfactualThought();
        serverData = new ServerData();
    }

    // ApplicationStatus is a singleton
    private static ApplicationStatus instance = null;

    // synchronized is necessary for thread safety
    public static synchronized ApplicationStatus getInstance( Context context ) throws Exception
    {
        if( null == instance )
        {
            instance = loadApplicationStatus( context );
        }

        return instance;
    }




    //
    //Daily and weekly evaluations
    //
    public ArrayList<DailyEvaluation> dailyEvaluations;

    public boolean pendingDailyEvaluationsExist() throws IOException, JSONException, ConversionException
    {
        int before = dailyEvaluations.size();

        JSONArrayConverterActionPlan jacap = new JSONArrayConverterActionPlan();
        ActionPlansStorage aps = new ActionPlansStorage( context );
        aps.read( jacap );

        JSONArrayConverterCopingPlan jaccp = new JSONArrayConverterCopingPlan();
        CopingPlansStorage cps = new CopingPlansStorage( context );
        cps.read( jaccp );

        ArrayList<IfThenPlan> plans = new ArrayList<>();
        for( ActionPlan actionPlan : jacap.getActionPlans() )
        {
            plans.add( actionPlan );
        }
        for( CopingPlan copingPlan: jaccp.getCopingPlans() )
        {
            plans.add( copingPlan );
        }

        dailyEvaluations = DailyEvaluation.createMissing( dailyEvaluations, plans );

        if( before != dailyEvaluations.size() )
        {
            DailyEvaluationsStorage storage = new DailyEvaluationsStorage( context );
            storage.write( new JSONArrayConverterDailyEvaluation( dailyEvaluations ) );
        }

        return DailyEvaluation.pendingExist( dailyEvaluations );
    }

    public void scoreDailyEvaluation( int id, boolean success )
        throws Exception
    {
        for( int i = 0 ; i < dailyEvaluations.size() ; i++ )
        {
            DailyEvaluation evaluation = dailyEvaluations.get( i );
            if( id == evaluation.id )
            {
                evaluation.evaluate( success );
                DailyEvaluationsStorage s = new DailyEvaluationsStorage( context );
                s.write( new JSONArrayConverterDailyEvaluation( dailyEvaluations ) );
                state.moveNext();
                return;
            }
        }

        throw new Exception( "Daily evaluation id " + String.valueOf( id ) + " not found." );
    }

    public ArrayList<WeeklyEvaluation> weeklyEvaluations;

    public boolean pendingWeeklyEvaluationsExist() throws IOException, JSONException, ConversionException
    {
        int before = weeklyEvaluations.size();

        weeklyEvaluations = WeeklyEvaluation.createMissing(
            startDate,
            new Date(),
            weeklyEvaluations,
            problematicBehaviors.contains( Behavior.DIET ),
            problematicBehaviors.contains( Behavior.SMOKING ),
            problematicBehaviors.contains( Behavior.ACTIVITY ),
            problematicBehaviors.contains( Behavior.ALCOHOL )
        );

        if( before != weeklyEvaluations.size() )
        {
            WeeklyEvaluationsStorage wes = new WeeklyEvaluationsStorage( context );
            wes.write( new JSONArrayConverterWeeklyEvaluation( weeklyEvaluations ) );
        }

        return WeeklyEvaluation.pendingExist( weeklyEvaluations );
    }

    public void scoreWeeklyEvaluation( int weekOfYear, int year, int dietScore, int physicalActivityScore, int alcoholScore, int smokingScore )
        throws Exception
    {
        for( int i = 0 ; i < weeklyEvaluations.size() ; i++ )
        {
            WeeklyEvaluation evaluation = weeklyEvaluations.get( i );
            if( evaluation.getWeekOfYear() == weekOfYear && evaluation.getYear() == year )
            {
                evaluation.score( dietScore, physicalActivityScore, alcoholScore, smokingScore );
                WeeklyEvaluationsStorage wes = new WeeklyEvaluationsStorage( context );
                wes.write( new JSONArrayConverterWeeklyEvaluation( weeklyEvaluations ) );

                state.moveNext();
                return;
            }
        }

        throw new Exception( "Weekly evaluation for week " + String.valueOf( weekOfYear ) + ", and year " + String.valueOf( weekOfYear ) + " not found." );
    }




    //
    // reading and writing to storage
    //
    private static final String FILENAME = "application_status.json";

    private static ApplicationStatus loadApplicationStatus( Context context ) throws Exception
    {
        ApplicationStatus as = new ApplicationStatus( context );

        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if( !file.exists() || 0 == file.length() )
        {
            as.save();
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

//            String[] dateParts =  jsonState.getString( "start_date" ).split( "-" );
//
//            final Calendar cal = Calendar.getInstance();
//            cal.set( Calendar.YEAR, Integer.parseInt( dateParts[0] ) );
//            cal.set( Calendar.MONTH, Integer.parseInt( dateParts[1] ) );
//            cal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( dateParts[2] ) );
//            cal.set( Calendar.HOUR_OF_DAY, 0 );
//            cal.set( Calendar.MINUTE, 0 );
//            cal.set( Calendar.SECOND, 0 );
//            cal.set( Calendar.MILLISECOND, 0 );
//            as.startDate = cal.getTime();
            as.startDate = ahat.mmsnap.json.Util.dateFromJsonString( jsonState.getString( "start_date" ) );

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

            as.intentionsAndPlans.plan_exercise_when = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_when" );
            as.intentionsAndPlans.plan_exercise_pastweek = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_pastweek" );
            as.intentionsAndPlans.plan_exercise_where = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_where" );
            as.intentionsAndPlans.plan_exercise_how = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_how" );
            as.intentionsAndPlans.plan_exercise_often = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_often" );
            as.intentionsAndPlans.plan_exercise_whom = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_whom" );
            as.intentionsAndPlans.plan_exercise_interfere = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_interfere" );
            as.intentionsAndPlans.plan_exercise_setbacks = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_setbacks" );
            as.intentionsAndPlans.plan_exercise_situations = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_situations" );
            as.intentionsAndPlans.plan_exercise_opportunities = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_opportunities" );
            as.intentionsAndPlans.plan_exercise_lapses = jsonState.getBoolean( "intentionsAndPlans.plan_exercise_lapses" );
            as.intentionsAndPlans.plan_intend_times = jsonState.getBoolean( "intentionsAndPlans.plan_intend_times" );
            as.intentionsAndPlans.plan_intend_sweat = jsonState.getBoolean( "intentionsAndPlans.plan_intend_sweat" );
            as.intentionsAndPlans.plan_intend_regularly = jsonState.getBoolean( "intentionsAndPlans.plan_intend_regularly" );
            as.intentionsAndPlans.plan_intend_active = jsonState.getBoolean( "intentionsAndPlans.plan_intend_active" );
            as.intentionsAndPlans.plan_intend_leisure = jsonState.getBoolean( "intentionsAndPlans.plan_intend_leisure" );
            as.intentionsAndPlans.plan_intend_rehabilitation = jsonState.getBoolean( "intentionsAndPlans.plan_intend_rehabilitation" );

            as.selfRatedHealth.one_condition_more_serious = jsonState.getInt( "selfRatedHealth.one_condition_more_serious" );
            as.selfRatedHealth.time_spent_managing = jsonState.getInt( "selfRatedHealth.time_spent_managing" );
            as.selfRatedHealth.feel_overwhelmed = jsonState.getInt( "selfRatedHealth.feel_overwhelmed" );
            as.selfRatedHealth.causes_are_linked = jsonState.getInt( "selfRatedHealth.causes_are_linked" );
            as.selfRatedHealth.difficult_all_medications = jsonState.getInt( "selfRatedHealth.difficult_all_medications" );
            as.selfRatedHealth.limited_activities = jsonState.getInt( "selfRatedHealth.limited_activities" );
            as.selfRatedHealth.different_medications_problems = jsonState.getInt( "selfRatedHealth.different_medications_problems" );
            as.selfRatedHealth.mixing_medications = jsonState.getInt( "selfRatedHealth.mixing_medications" );
            as.selfRatedHealth.less_effective_treatments = jsonState.getInt( "selfRatedHealth.less_effective_treatments" );
            as.selfRatedHealth.one_cause_another = jsonState.getInt( "selfRatedHealth.one_cause_another" );
            as.selfRatedHealth.one_dominates = jsonState.getInt( "selfRatedHealth.one_dominates" );
            as.selfRatedHealth.conditions_interact = jsonState.getInt( "selfRatedHealth.conditions_interact" );
            as.selfRatedHealth.difficult_best_treatment = jsonState.getInt( "selfRatedHealth.difficult_best_treatment" );
            as.selfRatedHealth.reduced_social_life = jsonState.getInt( "selfRatedHealth.reduced_social_life" );
            as.selfRatedHealth.unhappy = jsonState.getInt( "selfRatedHealth.unhappy" );
            as.selfRatedHealth.anxious = jsonState.getInt( "selfRatedHealth.anxious" );
            as.selfRatedHealth.angry = jsonState.getInt( "selfRatedHealth.angry" );
            as.selfRatedHealth.sad = jsonState.getInt( "selfRatedHealth.sad" );
            as.selfRatedHealth.irritable = jsonState.getInt( "selfRatedHealth.irritable" );
            as.selfRatedHealth.sad_struggle = jsonState.getInt( "selfRatedHealth.sad_struggle" );
            
            DailyEvaluationsStorage des = new DailyEvaluationsStorage( context );
            JSONArrayConverterDailyEvaluation jacde = new JSONArrayConverterDailyEvaluation();
            des.read( jacde );
            as.dailyEvaluations = jacde.getDailyEvaluations();

            WeeklyEvaluationsStorage wes = new WeeklyEvaluationsStorage( context );
            JSONArrayConverterWeeklyEvaluation jc = new JSONArrayConverterWeeklyEvaluation();
            wes.read( jc );
            as.weeklyEvaluations = jc.getWeeklyEvaluations();

            JSONObject jsonCounterfactual = jsonState.getJSONObject( "counterfactual" );
            as.counterfactualThought.ifStatement = jsonCounterfactual.getString( "if" );
            as.counterfactualThought.thenStatement = jsonCounterfactual.getString( "then" );
            as.counterfactualThought.active = jsonCounterfactual.getBoolean( "active" );

            as.serverData.fromJson( jsonState.getJSONObject( "server_data" ) );
        }
        finally
        {
            is.close();
        }
        return as;
    }

    public void save() throws IOException, JSONException, ConversionException
    {
        String filePath = context.getFilesDir().getPath() + "/" + FILENAME;
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
        JSONObject o = new JSONObject();

//        Calendar cal = Calendar.getInstance();
//        cal.setTime( startDate );
//        o.put( "start_date", cal.get( Calendar.YEAR ) + "-" + cal.get( Calendar.MONTH ) + "-" + cal.get( Calendar.DAY_OF_MONTH ) );
        o.put( "start_date", ahat.mmsnap.json.Util.toJsonString( startDate ) );

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

        o.put( "intentionsAndPlans.plan_exercise_when", intentionsAndPlans.plan_exercise_when );
        o.put( "intentionsAndPlans.plan_exercise_pastweek", intentionsAndPlans.plan_exercise_pastweek );
        o.put( "intentionsAndPlans.plan_exercise_where", intentionsAndPlans.plan_exercise_where );
        o.put( "intentionsAndPlans.plan_exercise_how", intentionsAndPlans.plan_exercise_how );
        o.put( "intentionsAndPlans.plan_exercise_often", intentionsAndPlans.plan_exercise_often );
        o.put( "intentionsAndPlans.plan_exercise_whom", intentionsAndPlans.plan_exercise_whom );
        o.put( "intentionsAndPlans.plan_exercise_interfere", intentionsAndPlans.plan_exercise_interfere );
        o.put( "intentionsAndPlans.plan_exercise_setbacks", intentionsAndPlans.plan_exercise_setbacks );
        o.put( "intentionsAndPlans.plan_exercise_situations", intentionsAndPlans.plan_exercise_situations );
        o.put( "intentionsAndPlans.plan_exercise_opportunities", intentionsAndPlans.plan_exercise_opportunities );
        o.put( "intentionsAndPlans.plan_exercise_lapses", intentionsAndPlans.plan_exercise_lapses );
        o.put( "intentionsAndPlans.plan_intend_times", intentionsAndPlans.plan_intend_times );
        o.put( "intentionsAndPlans.plan_intend_sweat", intentionsAndPlans.plan_intend_sweat );
        o.put( "intentionsAndPlans.plan_intend_regularly", intentionsAndPlans.plan_intend_regularly );
        o.put( "intentionsAndPlans.plan_intend_active", intentionsAndPlans.plan_intend_active );
        o.put( "intentionsAndPlans.plan_intend_leisure", intentionsAndPlans.plan_intend_leisure );
        o.put( "intentionsAndPlans.plan_intend_rehabilitation", intentionsAndPlans.plan_intend_rehabilitation );

        o.put( "selfRatedHealth.one_condition_more_serious", selfRatedHealth.one_condition_more_serious );
        o.put( "selfRatedHealth.time_spent_managing", selfRatedHealth.time_spent_managing );
        o.put( "selfRatedHealth.feel_overwhelmed", selfRatedHealth.feel_overwhelmed );
        o.put( "selfRatedHealth.causes_are_linked", selfRatedHealth.causes_are_linked );
        o.put( "selfRatedHealth.difficult_all_medications", selfRatedHealth.difficult_all_medications );
        o.put( "selfRatedHealth.limited_activities", selfRatedHealth.limited_activities );
        o.put( "selfRatedHealth.different_medications_problems", selfRatedHealth.different_medications_problems );
        o.put( "selfRatedHealth.mixing_medications", selfRatedHealth.mixing_medications );
        o.put( "selfRatedHealth.less_effective_treatments", selfRatedHealth.less_effective_treatments );
        o.put( "selfRatedHealth.one_cause_another", selfRatedHealth.one_cause_another );
        o.put( "selfRatedHealth.one_dominates", selfRatedHealth.one_dominates );
        o.put( "selfRatedHealth.conditions_interact", selfRatedHealth.conditions_interact );
        o.put( "selfRatedHealth.difficult_best_treatment", selfRatedHealth.difficult_best_treatment );
        o.put( "selfRatedHealth.reduced_social_life", selfRatedHealth.reduced_social_life );
        o.put( "selfRatedHealth.unhappy", selfRatedHealth.unhappy );
        o.put( "selfRatedHealth.anxious", selfRatedHealth.anxious );
        o.put( "selfRatedHealth.angry", selfRatedHealth.angry );
        o.put( "selfRatedHealth.sad", selfRatedHealth.sad );
        o.put( "selfRatedHealth.irritable", selfRatedHealth.irritable );
        o.put( "selfRatedHealth.sad_struggle", selfRatedHealth.sad_struggle );

        JSONObject jsonCounterfactual = new JSONObject();
        jsonCounterfactual.put( "if", counterfactualThought.ifStatement );
        jsonCounterfactual.put( "then", counterfactualThought.thenStatement );
        jsonCounterfactual.put( "active", counterfactualThought.active );
        o.put( "counterfactual", jsonCounterfactual );

        o.put( "server_data", serverData.toJson() );

        try
        {
            fos.write( o.toString( 2 ).getBytes() );
        }
        finally
        {
            fos.close();
        }

        DailyEvaluationsStorage des = new DailyEvaluationsStorage( context );
        des.write( new JSONArrayConverterDailyEvaluation( dailyEvaluations ) );
        WeeklyEvaluationsStorage wes = new WeeklyEvaluationsStorage( context );
        wes.write( new JSONArrayConverterWeeklyEvaluation( weeklyEvaluations ) );
    }



    //
    // The state of the applicationstatus follows the GoF state pattern
    // States are inner class to be able to access the private members of applicationstatus
    //
    State state;
    public void setState( State state ) { this.state = state ;}
    public State getState() { return state; }

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
            try
            {
                if( pendingWeeklyEvaluationsExist() )
                {
                    applicationStatus.setState( new WeeklyEvaluationPending( applicationStatus ) );
                    return true;
                }
                else if( pendingDailyEvaluationsExist() )
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
            catch( Exception e )
            {
                return false;
            }
        }

        protected boolean programDurationExpired()
        {
            Calendar c = Calendar.getInstance();
            c.setTime( startDate );
            c.add( Calendar.DATE, DURATION_DAYS );
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
            try
            {
                if( pendingWeeklyEvaluationsExist() )
                {
                    return false;
                }
                else if( pendingDailyEvaluationsExist() )
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
            catch( Exception e )
            {
                return false;
            }
        }
    }
    public class DailyEvaluationPending extends InOrder
    {
        static final String NAME = "DaillyEvaluationPending";
        public String name() { return NAME; }
        DailyEvaluationPending( ApplicationStatus applicationStatus ) { super( applicationStatus ); }
        public boolean moveNext()
        {
            try
            {
                if( pendingDailyEvaluationsExist() )
                {
                    return false;
                }
                else if( pendingWeeklyEvaluationsExist() )
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
            catch( Exception e )
            {
                return false;
            }
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

}
