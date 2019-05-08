package ahat.mmsnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ahat.mmsnap.json.ActionPlansStorage;
import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.JSONArrayConverterActionPlan;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.models.ActionPlan;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.CopingPlan;
import ahat.mmsnap.models.DailyEvaluation;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.notifications.PendingEvaluationsAlarmReceiver;
import ahat.mmsnap.notifications.ReminderNotificationServiceStarterReceiver;
import ahat.mmsnap.rest.RESTAlarmReceiver;

import static ahat.mmsnap.models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.WEDNESDAY;


public class MainActivity extends StateActivity //AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, android.view.View.OnClickListener
{

    private TextView counterfactualTextView;
//    private Button educationButton;
//    private Button ifThenButton;
//    private Button achievementsButton;

    public enum Display { TODAY, ATTENTION, ACHIEVEMENTS, SECTIONS };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected( @NonNull MenuItem item )
        {
            switch( item.getItemId() )
            {
                case R.id.navigation_today:
                    show( Display.TODAY );
                    return true;
                case R.id.navigation_attention:
                    show( Display.ATTENTION );
                    return true;
                case R.id.navigation_achievements:
                    show( Display.ACHIEVEMENTS );
                    return true;
                case R.id.navigation_sections:
                    show( Display.SECTIONS );
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setTitle( R.string.title_activity_mmsnap );

        AppCompatDelegate.setCompatVectorFromResourcesEnabled( true );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        BottomNavigationView bottomNavigationView = findViewById( R.id.bottom_navigation );
        bottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        //buttons events
//        Button mmsnapButton = findViewById( R.id.mmsnap_btn );
//        mmsnapButton.setOnClickListener( this );
//        Button assessmentsButton = findViewById( R.id.assessments_btn );
//        assessmentsButton.setOnClickListener( this );
//        educationButton = findViewById( R.id.education_btn );
//        educationButton.setOnClickListener( this );
//        ifThenButton = findViewById( R.id.if_then_btn );
//        ifThenButton.setOnClickListener( this );
//        achievementsButton = findViewById( R.id.achievements_btn );
//        achievementsButton.setOnClickListener( this );

        findViewById( R.id.mmsnap_for_btn ).setOnClickListener( this );
        findViewById( R.id.mmsnap_mm_btn ).setOnClickListener( this );
        findViewById( R.id.mmsnap_assoc_btn ).setOnClickListener( this );
        findViewById( R.id.mmsnap_mb_btn ).setOnClickListener( this );
        findViewById( R.id.mmsnap_app_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_illness_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_risk_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_efficacy_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_plans_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_health_btn ).setOnClickListener( this );
        findViewById( R.id.assessments_weekly_btn ).setOnClickListener( this );
        findViewById( R.id.edu_what_btn ).setOnClickListener( this );
        findViewById( R.id.edu_if_btn ).setOnClickListener( this );
        findViewById( R.id.edu_then_btn ).setOnClickListener( this );
        findViewById( R.id.edu_test_btn ).setOnClickListener( this );
        findViewById( R.id.if_then_counterfactual_btn ).setOnClickListener( this );
        findViewById( R.id.if_then_action_btn ).setOnClickListener( this );
        findViewById( R.id.if_then_coping_btn ).setOnClickListener( this );
        findViewById( R.id.daily_evaluations_btn ).setOnClickListener( this );


        // testing is visible/available only in the debug version
        if( ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) == 0 )
        {
            findViewById( R.id.nav_testing ).setVisibility( View.GONE );
        }

        // start the alarm that will trigger notifications if there are pending daily or weekly evaluations
        PendingEvaluationsAlarmReceiver.setupAlarm( this );

        // start the alarm that will trigger REST calls
        RESTAlarmReceiver.setupAlarm( this );

        try
        {
            ReminderNotificationServiceStarterReceiver.startReminderAlarms( this );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not start reminder alarms for active plans", Snackbar.LENGTH_SHORT )
                    .show();
        }

        // the application starts with TODAY's plans in view
//        show( Display.TODAY );
        Display display = Display.TODAY;

        Bundle b = getIntent().getExtras();
        if( null != b && b.containsKey(  "display" ) )
        {
            display = (Display) b.get( "display" );
            BottomNavigationMenuView bottomNavigationMenu = findViewById( R.id.bottom_navigation );
            bottomNavigationView.getMenu().getItem( display.ordinal() ).setChecked( true );
        }
        show( display );

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );

            // setup achievements
            try
            {
                final ArrayList<DailyEvaluation> evaluations = as.dailyEvaluations;
                ListView list = findViewById( R.id.achievements_list );
                AchievementsListAdapter adapter = new AchievementsListAdapter( this, evaluations, R.id.achievements_list );
                list.setAdapter( adapter );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                Snackbar.make( findViewById( android.R.id.content ), "Could not load daily evaluations", Snackbar.LENGTH_SHORT ).show();
            }


            // setup the counterfactual thought message
            counterfactualTextView = findViewById( R.id.main_counterfactual );

            if( as.counterfactualThought.active && as.counterfactualThought.ifStatement.trim().length() > 0 && as.counterfactualThought.thenStatement.trim().length() > 0 )
            {
                String text  = "<strong>IF</strong>&nbsp;" + as.counterfactualThought.ifStatement +
                               "&nbsp;<strong>THEN</strong>&nbsp;" + as.counterfactualThought.thenStatement;
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    counterfactualTextView.setText( Html.fromHtml( text, Html.FROM_HTML_MODE_COMPACT ) );
                }
                else
                {
                    counterfactualTextView.setText( Html.fromHtml( text ) );
                }
            }
            else
            {
                counterfactualTextView.setVisibility( View.GONE );
            }

            try
            {
                // setup the not enough weekly plans message
                ArrayList<IfThenPlan> weekPlans = loadWeeksActivePlans();
                int days = 0;
                for( IfThenPlan plan : weekPlans )
                {
                    days += plan.days.size();
                }
                findViewById( R.id.main_message_layout ).setVisibility( days >= ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.GONE : View.VISIBLE );

                // setup today's plans list
                ArrayList<IfThenPlan> todaysPlans = new ArrayList<>();
                IfThenPlan.WeekDay today = getTodaysDay();
                for( IfThenPlan plan : weekPlans )
                {
                    if( plan.hasDay( today ) )
                    {
                        todaysPlans.add( plan );
                    }
                }
                ListView list = findViewById( R.id.todays_listview );
                TodaysListAdapter adapter = new TodaysListAdapter( this, todaysPlans );
                list.setAdapter( adapter );

                // setup attention
                Calendar todayCal = Calendar.getInstance();
                for( ApplicationStatus.Behavior behavior : ApplicationStatus.Behavior.values() )
                {
                    if( as.problematicBehaviors.contains( behavior ) )
                    {
                        makeAttentionBehaviorVisible( behavior );
                        int totalPlans = countTotalPlans( weekPlans, behavior );
                        int remainingPlans = countRemainingPlans( weekPlans, behavior );
                        int successfulPlans = countSuccessfulWeekEvaluations( as.dailyEvaluations, todayCal.get( Calendar.WEEK_OF_YEAR ), behavior );
                        setRemainingText( totalPlans, remainingPlans, behavior );
                        setTargetText( successfulPlans, ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK, behavior );
                    }
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
                Snackbar.make( findViewById( android.R.id.content ), "Could not load active plans", Snackbar.LENGTH_SHORT )
                        .show();
            }

            applyLocalStatePolicy();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not load application status", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }

    }

    private void applyLocalStatePolicy()
    {
        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            TextView messageView = findViewById( R.id.message );

            if( ApplicationStatus.NoInitialAssessments.NAME.equals( as.getState().name() ) ||
                ApplicationStatus.NoFinalAssessments.NAME.equals( as.getState().name() ) )
            {
                show( Display.SECTIONS );
                counterfactualTextView.setVisibility( View.GONE );
                findViewById( R.id.education_layout ).setVisibility( View.INVISIBLE );  // educationButton.setVisibility( View.INVISIBLE );
                findViewById( R.id.if_then_layout ).setVisibility( View.INVISIBLE );  // ifThenButton.setVisibility( View.INVISIBLE );
//                achievementsButton.setVisibility( View.INVISIBLE );
                findViewById( R.id.bottom_navigation ).setVisibility( View.GONE );
                findViewById( R.id.main_message_layout ).setVisibility( View.GONE );
            }


            if( ApplicationStatus.NoInitialAssessments.NAME.equals( as.getState().name() ) )
            {
                messageView.setText( R.string.please_complete_the_initial_assessments );
                messageView.setVisibility( View.VISIBLE );

                findViewById( R.id.illness_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.risk_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.efficacy_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.plans_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.health_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? View.GONE : View.VISIBLE );

                findViewById( R.id.illness_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.risk_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.efficacy_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.plans_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.health_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? View.VISIBLE : View.GONE );

            }
            else if( ApplicationStatus.NoFinalAssessments.NAME.equals( as.getState().name() ) )
            {
                messageView.setText( R.string.please_complete_the_final_assessments );
                messageView.setVisibility( View.VISIBLE );

                findViewById( R.id.illness_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.risk_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.efficacy_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.plans_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? View.GONE : View.VISIBLE );
                findViewById( R.id.health_warning_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? View.GONE : View.VISIBLE );

                findViewById( R.id.illness_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.risk_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.efficacy_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.plans_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? View.VISIBLE : View.GONE );
                findViewById( R.id.health_ok_img ).setVisibility( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? View.VISIBLE : View.GONE );

            }
            else
            {
                findViewById( R.id.message_layout ).setVisibility( View.GONE );

                findViewById( R.id.efficacy_lock_img ).setVisibility( View.VISIBLE );
                findViewById( R.id.plans_lock_img ).setVisibility( View.VISIBLE );
                findViewById( R.id.health_lock_img ).setVisibility( View.VISIBLE );
            }

        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not load application status", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }
    }

    private void setTargetText( int successfulPlans, int targetPlans, ApplicationStatus.Behavior behavior )
    {
        TextView textView = null;
        switch( behavior )
        {
            case DIET:
                textView = findViewById( R.id.attention_target_diet );
                break;
            case ACTIVITY:
                textView = findViewById( R.id.attention_target_activity );
                break;
            case ALCOHOL:
                textView = findViewById( R.id.attention_target_alcohol );
                break;
            case SMOKING:
                textView = findViewById( R.id.attention_target_smoking );
                break;
        }

        int missingPlans = successfulPlans > targetPlans ? 0 : targetPlans - successfulPlans;
        boolean achieved = false;
        String text = "You need to meet " + String.valueOf( missingPlans ) + " more plans to achieve this week's target.";
        if( 0 == missingPlans )
        {
            achieved = true;
            text = "You have achieved this week's target!";
        }
        else if( 1 == missingPlans )
        {
            text = "You need to meet one more plan to achieve this week's target.";
        }

        if( null != textView )
        {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                textView.setText( Html.fromHtml( text, Html.FROM_HTML_MODE_COMPACT ) );
            }
            else
            {
                textView.setText( Html.fromHtml( text ) );
            }
        }

        if( achieved )
        {
            Drawable check = ContextCompat.getDrawable( this, R.drawable.ic_check_24dp );
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds( check, null, null, null );
        }
    }

    private void setRemainingText( int totalPlans, int remainingPlans, ApplicationStatus.Behavior behavior )
    {
        TextView textView = null;
        switch( behavior )
        {
            case DIET:
                textView = findViewById( R.id.attention_remaining_diet );
                break;
            case ACTIVITY:
                textView = findViewById( R.id.attention_remaining_activity );
                break;
            case ALCOHOL:
                textView = findViewById( R.id.attention_remaining_alcohol );
                break;
            case SMOKING:
                textView = findViewById( R.id.attention_remaining_smoking );
                break;
        }

        String text = String.valueOf( remainingPlans ) + " plans have not been met yet.";
        boolean error = false;
        boolean complete = false;
        if( 0 == totalPlans )
        {
            text = "You have <strong>NO</strong> plans in this week!";
            error = true;
        }
        else if( 0 == remainingPlans )
        {
            text = "All plans for this week have been met!";
            complete = true;
        }
        else if( 1 == remainingPlans )
        {
            text = "One plan remains to be met yet.";
        }

        if( null != textView )
        {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                textView.setText( Html.fromHtml( text, Html.FROM_HTML_MODE_COMPACT ) );
            }
            else
            {
                textView.setText( Html.fromHtml( text ) );
            }
        }

        if( error )
        {
            Drawable dr = ContextCompat.getDrawable( this, android.R.drawable.ic_dialog_alert );
            Bitmap bitmap = ( (BitmapDrawable) dr).getBitmap();
            // Scale it to 80 x 80
            Drawable warning = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));
            warning.setColorFilter(new PorterDuffColorFilter( getResources().getColor( R.color.yellow_warning ), PorterDuff.Mode.MULTIPLY));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds( warning, null, null, null );
        }
        else if( complete )
        {
            Drawable dr = ContextCompat.getDrawable( this, R.drawable.ic_check_24dp );
            Bitmap bitmap = ( (BitmapDrawable) dr).getBitmap();
            // Scale it to 80 x 80
            Drawable check = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds( check, null, null, null );
        }
    }

    private int countTotalPlans( ArrayList<IfThenPlan> weekPlans, ApplicationStatus.Behavior behavior )
    {
        int count = 0 ;
        for( IfThenPlan plan : weekPlans )
        {
            if( plan.isTarget( behavior ) )
            {
                count += plan.days.size();
            }
        }
        return count;
    }

    private int countSuccessfulWeekEvaluations( ArrayList<DailyEvaluation> evaluations, int weekOfYear, ApplicationStatus.Behavior behavior )
    {
        int count = 0;

        for( DailyEvaluation evaluation : evaluations )
        {
            if( evaluation.plan.isTarget( behavior ) && evaluation.plan.weekOfYear == weekOfYear && evaluation.isSuccessful() )
            {
                count++;
            }
        }
        return count;
    }

    private int countRemainingPlans( ArrayList<IfThenPlan> weekPlans, ApplicationStatus.Behavior behavior )
    {
        int count = 0;
        IfThenPlan.WeekDay today = getTodaysDay( );
        for( IfThenPlan plan : weekPlans )
        {
            if( plan.isTarget( behavior ) )
            {
                for( IfThenPlan.WeekDay day : plan.days )
                {
                    if( day.ordinal() >= today.ordinal() )
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void makeAttentionBehaviorVisible( ApplicationStatus.Behavior behavior )
    {
        switch( behavior )
        {
            case DIET:
                findViewById( R.id.diet_layout ).setVisibility( View.VISIBLE );
                break;
            case SMOKING:
                findViewById( R.id.smoking_layout ).setVisibility( View.VISIBLE );
                break;
            case ACTIVITY:
                findViewById( R.id.physical_activity_layout ).setVisibility( View.VISIBLE );
                break;
            case ALCOHOL:
                findViewById( R.id.alcohol_layout ).setVisibility( View.VISIBLE );
                break;
        }
    }

    private IfThenPlan.WeekDay getTodaysDay()
    {
         Calendar c = Calendar.getInstance();
         c.setTime( new Date() );

        switch( c.get( Calendar.DAY_OF_WEEK ) )
        {
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return MONDAY;
            case Calendar.SATURDAY:
                return SATURDAY;
            case Calendar.SUNDAY:
                return SUNDAY;
        }

        return null;
    }

    private ArrayList<IfThenPlan> loadWeeksActivePlans() throws JSONException, IOException, ConversionException
    {
        ArrayList<IfThenPlan> plans = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        ActionPlansStorage aps = new ActionPlansStorage( this );
        JSONArrayConverterActionPlan jcap = new JSONArrayConverterActionPlan();
        aps.read( jcap );
        for( ActionPlan plan : jcap.getActionPlans() )
        {
            if( plan.year == c.get( Calendar.YEAR ) && plan.weekOfYear == c.get( Calendar.WEEK_OF_YEAR ) && plan.active )
            {
                plans.add( plan );
            }
        }

        CopingPlansStorage cps = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jccp = new JSONArrayConverterCopingPlan();
        cps.read( jccp );
        for( CopingPlan plan : jccp.getCopingPlans() )
        {
            if( plan.year == c.get( Calendar.YEAR ) && plan.weekOfYear == c.get( Calendar.WEEK_OF_YEAR )  && plan.active )
            {
                plans.add( plan );
            }
        }

        return plans;
    }

    @Override
    public void onClick( View view )
    {
        Intent intent;
        Bundle b = new Bundle();
        switch (view.getId()){
//            case R.id.mmsnap_btn:
//                intent = new Intent(this, MMSNAPActivity.class);
//                startActivity( intent );
//                break;
//            case R.id.assessments_btn:
//                intent = new Intent(this, AssessmentsActivity.class);
//                startActivity( intent );
//                break;
//            case R.id.education_btn:
//                intent = new Intent(this, EduActivity.class);
//                startActivity( intent );
//                break;
//            case R.id.if_then_btn:
//                intent = new Intent(this, IfThenActivity.class);
//                startActivity( intent );
//                break;
//            case R.id.achievements_btn:
//                intent = new Intent(this, AchievementsActivity.class);
//                startActivity( intent );
//                break;
            case R.id.mmsnap_for_btn:
                intent = new Intent(this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );
                b.putInt( "subcategory", 0 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_mm_btn:
                intent = new Intent(this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );
                b.putInt( "subcategory", 1 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_mb_btn:
                intent = new Intent(this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );
                b.putInt( "subcategory", 2 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_assoc_btn:
                intent = new Intent(this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );
                b.putInt( "subcategory", 3 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_app_btn:
                intent = new Intent(this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );
                b.putInt( "subcategory", 4 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.assessments_illness_btn:
                intent = new Intent( this, EQVASActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_risk_btn:
                intent = new Intent( this, HealthRiskActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_efficacy_btn:
                intent = new Intent( this, EfficacyActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_plans_btn:
                intent = new Intent( this, IntentionsAndPlansActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_health_btn:
                intent = new Intent( this, SelfRatedHealthActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_weekly_btn:
                intent = new Intent( this, WeeklyEvaluationsListActivity.class);
                startActivity( intent );
                break;
            case R.id.edu_what_btn:
                intent = new Intent( this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.EDU );
                b.putInt( "subcategory", 0 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_if_btn:
                intent = new Intent( this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.EDU );
                b.putInt( "subcategory", 1 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_then_btn:
                intent = new Intent( this, MMSNAPSubCategoryActivity.class);
                b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.EDU );
                b.putInt( "subcategory", 2 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_test_btn:
                intent = new Intent( this, TestActivity.class);
                startActivity( intent );
                break;
            case R.id.if_then_counterfactual_btn:
                startActivity( new Intent( this, CounterfactualDetailActivity.class) );
                break;
            case R.id.if_then_action_btn:
                startActivity( new Intent( this, ActionPlansActivity.class) );
                break;
            case R.id.if_then_coping_btn:
                startActivity( new Intent( this, CopingPlansActivity.class) );
                break;
            case R.id.daily_evaluations_btn:
                startActivity( new Intent( this, DailyEvaluationsListActivity.class) );
                break;
            default:
                break;
        }
    }

    private void show( Display display )
    {
        switch( display )
        {
            case TODAY:
                findViewById( R.id.main_today_layout ).setVisibility( View.VISIBLE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_achievements_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_sections_layout ).setVisibility( View.GONE );
                getSupportActionBar().setSubtitle( R.string.title_activity_todays_plans );
                break;
            case ATTENTION:
                findViewById( R.id.main_today_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.VISIBLE );
                findViewById( R.id.main_achievements_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_sections_layout ).setVisibility( View.GONE );
                getSupportActionBar().setSubtitle( R.string.attention_frame_title );
                break;
            case ACHIEVEMENTS:
                findViewById( R.id.main_today_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_achievements_layout ).setVisibility( View.VISIBLE );
                findViewById( R.id.main_sections_layout ).setVisibility( View.GONE );
                getSupportActionBar().setSubtitle( R.string.title_activity_achievements );
                break;
            case SECTIONS:
                findViewById( R.id.main_today_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_achievements_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_sections_layout ).setVisibility( View.VISIBLE );
                getSupportActionBar().setSubtitle( R.string.main_subtitle_sections );
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if( drawer.isDrawerOpen( GravityCompat.START ) )
        {
            drawer.closeDrawer( GravityCompat.START );
        }
        else
        {
//            super.onBackPressed();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );
            alertDialog.setNegativeButton( android.R.string.no, null )
                       .setTitle("Exit MMSNAP")
                       .setMessage("Are you sure you wish to exit MMSNAP?" )
                       .setCancelable(true)
                       .setPositiveButton( android.R.string.yes,
                                           new DialogInterface.OnClickListener() {
                                               public void onClick( DialogInterface dialog, int which) {
                                                   exit();
                                               }
                                           })
                       .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings )
        {
            startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item )
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if( id == R.id.nav_camera )
//        {
//            // Handle the camera action
//        }
//        else if( id == R.id.nav_gallery )
//        {
//
//        }
//        else if( id == R.id.nav_slideshow )
//        {
//
//        }
//        else if( id == R.id.nav_manage )
//        {
//
//        }
        if( id == R.id.nav_testing )
        {
            startActivity( new Intent( this, TestingActivity.class ) );
        }
        else if( id == R.id.nav_exit )
        {
            exit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void exit()
    {
        finishAffinity(); // Close all activites
        System.exit(0 );  // Releasing resources
    }
}
