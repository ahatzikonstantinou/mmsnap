package ahat.mmsnap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.DailyEvaluation;
import ahat.mmsnap.Models.IfThenPlan;

import static ahat.mmsnap.Models.IfThenPlan.WeekDay.FRIDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.Models.IfThenPlan.WeekDay.WEDNESDAY;

public class DailyEvaluationsDetailActivity extends IfThenDetailActivity
{
    private DailyEvaluation evaluation = null;

    @Override
    protected int getActivityResLayout() { return R.layout.activity_daily_evaluations_detail; }

    @Override
    protected int getContentRootLayoutResId() { return android.R.id.content; }

    @Override
    protected IfThenPlan getIfThenItem()
    {
        if( null == evaluation )
        {
            if( getIntent().hasExtra( "evaluation" ) )
            {
                evaluation = (DailyEvaluation) getIntent().getSerializableExtra( "evaluation" );
                return evaluation.plan;
            }
        }

        return null;
    }

    @Override
    protected Class<?> getListActivityClass() { return DailyEvaluationsListActivity.class; }

    @Override
    protected String getSaveErrorMessage()
    {
        return "Could not save evaluation";
    }

    @Override
    protected void saveItem() throws IOException, JSONException, ConversionException
    {
        // just ignore this. We have other buttons that directly score the evaluation.
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        getSupportActionBar().setSubtitle( R.string.title_activity_action_plans );

        // hide the submit button, which is found in the action plan layout included in our layout
        findViewById( R.id.save ).setVisibility( View.GONE );

        evaluation = (DailyEvaluation) getIntent().getSerializableExtra( "evaluation" );

        //setup the layouts and textviews
        if( evaluation.plan instanceof ActionPlan )
        {
            findViewById( R.id.action_plans_layout ).setVisibility( View.VISIBLE );
            EditText copingIfStatementTextView = findViewById( R.id.item_coping_plan_if_statement );
            EditText copingThenStatementTextView = findViewById( R.id.item_coping_plan_then_statement );
            copingIfStatementTextView.setText( ( (ActionPlan) evaluation.plan ).copingIfStatement );
            copingThenStatementTextView.setText( ( (ActionPlan) evaluation.plan ).copingThenStatement );
        }
        else if( evaluation.plan instanceof CopingPlan )
        {
            findViewById( R.id.coping_plan_layout ).setVisibility( View.VISIBLE );
        }

        findViewById( R.id.evaluation_layout ).setVisibility( View.VISIBLE );

        //setup the success, fail buttons
        if( evaluation.isEvaluated() )
        {
            findViewById( R.id.fail_btn ).setVisibility( View.GONE );
            findViewById( R.id.success_btn ).setVisibility( View.GONE );
        }
        else
        {
            findViewById( R.id.fail_btn ).setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    scoreEvaluation( false );
                }
            } );
            findViewById( R.id.success_btn ).setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    scoreEvaluation( true );
                }
            } );
        }

        disableAllControls();

        //setup health buttons
        for( ApplicationStatus.Behavior behavior : ApplicationStatus.Behavior.values() )
        {
            if( !evaluation.plan.isTarget( behavior ) )
            {
                hideBehaviorUI( behavior );
            }
        }


        // dates setup
        findViewById( R.id.week_dates_layout ).setVisibility( View.GONE );
        findViewById( R.id.week_days_layout ).setVisibility( View.GONE );
        TextView dateTextView = findViewById( R.id.date_txt );
        dateTextView.setVisibility( View.VISIBLE );

        DateFormatSymbols dfs = new DateFormatSymbols();

        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, item.year );
        cal.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );
        cal.set( Calendar.DAY_OF_WEEK, evaluation.getWeekDay().toCalendarDayOfWeek() );

        dateTextView.setText( dfs.getShortWeekdays()[ cal.get( Calendar.DAY_OF_WEEK ) ]+ " " + cal.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getShortMonths()[ cal.get( Calendar.MONTH ) ] + " " + cal.get( Calendar.YEAR ) );



//        Drawable highlightBkg = getResources().getDrawable( R.drawable.custom_radio_highglight, null );
//        if( evaluation.getWeekDay() == MONDAY ) { findViewById( R.id.day_mon_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == TUESDAY ) { findViewById( R.id.day_tue_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == WEDNESDAY ) { findViewById( R.id.day_wed_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == THURSDAY ) { findViewById( R.id.day_thu_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == FRIDAY ) { findViewById( R.id.day_fri_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == SATURDAY ) { findViewById( R.id.day_sat_chk ).setBackground( highlightBkg  ); }
//        if( evaluation.getWeekDay() == SUNDAY ) { findViewById( R.id.day_sun_chk ).setBackground( highlightBkg  ); }
//
//        if( evaluation.getWeekDay() != MONDAY ) { findViewById( R.id.day_mon_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != TUESDAY ) { findViewById( R.id.day_tue_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != WEDNESDAY ) { findViewById( R.id.day_wed_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != THURSDAY ) { findViewById( R.id.day_thu_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != FRIDAY ) { findViewById( R.id.day_fri_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != SATURDAY ) { findViewById( R.id.day_sat_chk ).setVisibility( View.GONE ); }
//        if( evaluation.getWeekDay() != SUNDAY ) { findViewById( R.id.day_sun_chk ).setVisibility( View.GONE ); }
    }

    private void scoreEvaluation( boolean success )
    {
        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            as.scoreDailyEvaluation( evaluation.id, success );

            startActivity( getParentActivityIntent() );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "An error occurred while scoring the daily evaluation!", Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public void onBackPressed()
    {
        startActivity( getParentActivityIntent() );
    }

}
