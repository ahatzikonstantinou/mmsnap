package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import ahat.mmsnap.models.WeeklyEvaluation;

import static android.view.View.GONE;

public class WeeklyEvaluationActivity extends MassDisableActivity // AppCompatActivity
{

    private WeeklyEvaluation evaluation;

    protected int getActivityResLayout(){ return R.layout.activity_weekly_evaluation; }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_weekly_evaluation );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_weekly_evaluation );

        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                onBackPressed();
            }
        } );

        evaluation = (WeeklyEvaluation) getIntent().getSerializableExtra( "evaluation" );

        Calendar startCal = Calendar.getInstance();
        DateFormatSymbols dfs = new DateFormatSymbols();
        startCal.setTime( evaluation.start() );

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, startCal.get( Calendar.YEAR ) );
        c.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) );
        c.set( Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() );   // go to the first day of this week

        ( (TextView) findViewById( R.id.start_date ) )
            .setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                 dfs.getMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

        c.add( Calendar.DATE, 6);
        ( (TextView) findViewById( R.id.end_date ) )
            .setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                     dfs.getMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );


        findViewById( R.id.diet_layout ).setVisibility( evaluation.targetDiet ? View.VISIBLE : GONE );
        findViewById( R.id.physical_activity_layout ).setVisibility( evaluation.targetPhysicalActivity ? View.VISIBLE : GONE );
        findViewById( R.id.alcohol_layout ).setVisibility( evaluation.targetAlcohol ? View.VISIBLE : GONE );
        findViewById( R.id.smoking_layout ).setVisibility( evaluation.targetSmoking ? View.VISIBLE : GONE );

        if( evaluation.isScored() )
        {
            if( evaluation.targetDiet )
            {
                ( (RadioButton) ( (RadioGroup) findViewById( R.id.diet_radio_group ) ).getChildAt( evaluation.getDiet() ) ).setChecked( true );
            }
            if( evaluation.targetPhysicalActivity )
            {
                ( (RadioButton) ( (RadioGroup) findViewById( R.id.physical_activity_radio_group ) ).getChildAt( evaluation.getPhysicalActivity() ) ).setChecked( true );
            }
            if( evaluation.targetAlcohol )
            {
                ( (RadioButton) ( (RadioGroup) findViewById( R.id.alcohol_radio_group ) ).getChildAt( evaluation.getAlcohol() ) ).setChecked( true );
            }
            if( evaluation.targetSmoking )
            {
                ( (RadioButton) ( (RadioGroup) findViewById( R.id.smoking_radio_group ) ).getChildAt( evaluation.getSmoking() ) ).setChecked( true );
            }

            disableAllControls();
            findViewById( R.id.weekly_submit_btn ).setVisibility( GONE );
        }

        findViewById( R.id.weekly_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                try
                {
                    int dietScore = evaluation.targetDiet ? getScore( R.id.diet_radio_group ) : evaluation.getDiet();
                    int physicalActivityScore = evaluation.targetPhysicalActivity ? getScore( R.id.physical_activity_radio_group ) : evaluation.getPhysicalActivity();
                    int alcoholScore = evaluation.targetAlcohol ? getScore( R.id.alcohol_radio_group ) : evaluation.getAlcohol();
                    int smokingScore = evaluation.targetSmoking ? getScore( R.id.smoking_radio_group ) : evaluation.getSmoking();

                    if( dietScore > WeeklyEvaluation.MAX_SCORE ||
                        physicalActivityScore > WeeklyEvaluation.MAX_SCORE ||
                        alcoholScore > WeeklyEvaluation.MAX_SCORE ||
                        smokingScore > WeeklyEvaluation.MAX_SCORE
                    )
                    {
                        Snackbar.make( findViewById( android.R.id.content ), "Please enter a score for every target behavior in the weekly evaluation!", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
                    as.scoreWeeklyEvaluation(
                        evaluation.getWeekOfYear(),
                        evaluation.getYear(),
                        dietScore,
                        physicalActivityScore,
                        alcoholScore,
                        smokingScore
                    );

                    startActivity( new Intent( WeeklyEvaluationActivity.this, WeeklyEvaluationsListActivity.class ) );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Snackbar.make( findViewById( android.R.id.content ), "An error occurred while saving the weekly evaluation!", Snackbar.LENGTH_LONG).show();

                }
            }
        } );
    }

    private int getScore( int radioGroupResId )
    {
        RadioGroup rg = findViewById( radioGroupResId );
        int i = 0;
        for( ; i < rg.getChildCount() ; i++ )
        {
            if( ( (RadioButton) rg.getChildAt( i ) ).isChecked() )
            {
                break;
            }
        }

        return i;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable( "display", MainActivity.Display.SECTIONS );
        intent.putExtras( b );
        startActivity( intent );
    }

}
