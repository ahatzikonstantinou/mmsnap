package ahat.mmsnap;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterActionPlan;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.WEDNESDAY;

public class MainActivity extends StateActivity //AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, android.view.View.OnClickListener
{

    private enum Display { TODAY, ATTENTION, SECTIONS };

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setTitle( R.string.title_activity_mmsnap );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        //buttons events
        Button mmsnapButton = findViewById( R.id.mmsnap_btn );
        mmsnapButton.setOnClickListener( this );
        Button assessmentsButton = findViewById( R.id.assessments_btn );
        assessmentsButton.setOnClickListener( this );
        Button educationButton = findViewById( R.id.education_btn );
        educationButton.setOnClickListener( this );
        Button ifThenButton = findViewById( R.id.if_then_btn );
        ifThenButton.setOnClickListener( this );
        Button agentsButton = findViewById( R.id.agents_btn );
        agentsButton.setOnClickListener( this );

        findViewById( R.id.main_today_btn ).setOnClickListener( this );
        findViewById( R.id.main_attention_btn ).setOnClickListener( this );
        findViewById( R.id.main_sections_btn ).setOnClickListener( this );

        // the application starts with TODAY's plans in view
        show( Display.TODAY );

        try
        {
            // setup the counterfactual thought message
            TextView counterfactualTextView = findViewById( R.id.main_counterfactual );
            ApplicationStatus as = ApplicationStatus.getInstance( this );
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
                findViewById( R.id.main_message ).setVisibility( weekPlans.size() >= ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.GONE : View.VISIBLE );

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
                for( ApplicationStatus.Behavior behavior : ApplicationStatus.Behavior.values() )
                {
                    if( as.problematicBehaviors.contains( behavior ) )
                    {
                        makeAttentionBehaviorVisible( behavior );
                        int totalPlans = countTotalPlans( weekPlans, behavior );
                        int remainingPlans = countRemainingPlans( weekPlans, behavior );
                        int completedPlans = countCompletedPlans( weekPlans, behavior );
                        setRemainingText( totalPlans, remainingPlans, behavior );
                        setTargetText( completedPlans, ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK, behavior );
                    }
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
                Snackbar.make( findViewById( android.R.id.content ), "Could not load active plans", Snackbar.LENGTH_SHORT )
                        .show();
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

    private void setTargetText( int completedPlans, int targetPlans, ApplicationStatus.Behavior behavior )
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

        int missingPlans = completedPlans > targetPlans ? 0 : targetPlans - completedPlans;
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
            text = "You had <strong>NO</strong> plans in this week!";
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
            Drawable warning = ContextCompat.getDrawable( this, android.R.drawable.ic_dialog_alert );
            warning.setColorFilter(new PorterDuffColorFilter( getResources().getColor( R.color.yellow_warning, null ), PorterDuff.Mode.MULTIPLY));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds( warning, null, null, null );
        }
        else if( complete )
        {
            Drawable check = ContextCompat.getDrawable( this, R.drawable.ic_check_24dp );
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
                for( IfThenPlan.Day day: plan.days )
                {
                    count++;
                }
            }
        }
        return count;
    }

    private int countCompletedPlans( ArrayList<IfThenPlan> weekPlans, ApplicationStatus.Behavior behavior )
    {
        int count = 0;
        for( IfThenPlan plan : weekPlans )
        {
            for( IfThenPlan.Day day : plan.days )
            {
                if( day.isEvaluated() && day.isSuccessful() )
                {
                    count++;
                }
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
                for( IfThenPlan.Day day : plan.days )
                {
                    if( day.getWeekDay().ordinal() >= today.ordinal() )
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
            case ACTIVITY:
                findViewById( R.id.smoking_layout ).setVisibility( View.VISIBLE );
                break;
            case ALCOHOL:
                findViewById( R.id.physical_activity_layout ).setVisibility( View.VISIBLE );
                break;
            case SMOKING:
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
            if( plan.year == c.get( Calendar.YEAR ) && plan.weekOfYear == c.get( Calendar.WEEK_OF_YEAR ) )
            {
                plans.add( plan );
            }
        }

        CopingPlansStorage cps = new CopingPlansStorage( this );
        JSONArrayConverterCopingPlan jccp = new JSONArrayConverterCopingPlan();
        cps.read( jccp );
        for( CopingPlan plan : jccp.getCopingPlans() )
        {
            if( plan.year == c.get( Calendar.YEAR ) && plan.weekOfYear == c.get( Calendar.WEEK_OF_YEAR ) )
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
        switch (view.getId()){
            case R.id.mmsnap_btn:
                intent = new Intent(this, MMSNAPActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_btn:
                intent = new Intent(this, AssessmentsActivity.class);
                startActivity( intent );
                break;
            case R.id.education_btn:
                intent = new Intent(this, EduActivity.class);
                startActivity( intent );
                break;
            case R.id.if_then_btn:
                intent = new Intent(this, IfThenActivity.class);
                startActivity( intent );
                break;
                case R.id.agents_btn:
                intent = new Intent(this, AgentsActivity.class);
                startActivity( intent );
                break;
            case R.id.main_today_btn:
                show( Display.TODAY );
                break;
            case R.id.main_attention_btn:
                show( Display.ATTENTION );
                break;
            case R.id.main_sections_btn:
                show( Display.SECTIONS );
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
                findViewById( R.id.main_sections_layout ).setVisibility( View.GONE );
                getSupportActionBar().setSubtitle( R.string.title_activity_todays_plans );
                break;
            case ATTENTION:
                findViewById( R.id.main_today_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.VISIBLE );
                findViewById( R.id.main_sections_layout ).setVisibility( View.GONE );
                getSupportActionBar().setSubtitle( R.string.attention_frame_title );
                break;
            case SECTIONS:
                findViewById( R.id.main_today_layout ).setVisibility( View.GONE );
                findViewById( R.id.main_attention_layout ).setVisibility( View.GONE );
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
            super.onBackPressed();
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

    @SuppressWarnings( "StatementWithEmptyBody" )
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
//        else
        if( id == R.id.nav_exit )
        {
            Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
