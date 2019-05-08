package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class IntentionsAndPlansActivity extends MassDisableActivity // AppCompatActivity
{

    protected int getActivityResLayout(){ return R.layout.activity_plans; }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_plans );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_plans );


        findViewById( R.id.plan_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                try
                {
                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );

                    as.intentionsAndPlans.plan_exercise_when = ( ( CheckBox) findViewById( R.id.plan_exercise_when_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_pastweek = ( (CheckBox) findViewById( R.id.plan_exercise_pastweek_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_where = ( (CheckBox) findViewById( R.id.plan_exercise_where_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_how = ( (CheckBox) findViewById( R.id.plan_exercise_how_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_often = ( (CheckBox) findViewById( R.id.plan_exercise_often_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_whom = ( (CheckBox) findViewById( R.id.plan_exercise_whom_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_interfere = ( (CheckBox) findViewById( R.id.plan_exercise_interfere_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_setbacks = ( (CheckBox) findViewById( R.id.plan_exercise_setbacks_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_situations = ( (CheckBox) findViewById( R.id.plan_exercise_situations_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_opportunities = ( (CheckBox) findViewById( R.id.plan_exercise_opportunities_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_exercise_lapses = ( (CheckBox) findViewById( R.id.plan_exercise_lapses_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_times = ( (CheckBox) findViewById( R.id.plan_intend_times_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_sweat = ( (CheckBox) findViewById( R.id.plan_intend_sweat_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_regularly = ( (CheckBox) findViewById( R.id.plan_intend_regularly_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_active = ( (CheckBox) findViewById( R.id.plan_intend_active_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_leisure = ( (CheckBox) findViewById( R.id.plan_intend_leisure_cbx  ) ).isChecked();
                    as.intentionsAndPlans.plan_intend_rehabilitation = ( (CheckBox) findViewById( R.id.plan_intend_rehabilitation_cbx  ) ).isChecked();

                    as.serverData.add( as.intentionsAndPlans );
                    as.addAssessment( ApplicationStatus.Assessment.INTENTIONS );

                    finish();
                    Intent intent = new Intent( IntentionsAndPlansActivity.this, MainActivity.class );
                    Bundle b = new Bundle();
                    b.putSerializable( "display", MainActivity.Display.SECTIONS );
                    intent.putExtras( b );
                    startActivity( intent );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( IntentionsAndPlansActivity.this, "An error occured while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            if( ApplicationStatus.NoInitialAssessments.NAME != as.getState().name() &&
                ApplicationStatus.NoFinalAssessments.NAME != as.getState().name()
            )
            {
                findViewById( R.id.plan_submit_btn ).setVisibility( View.GONE );
                disableAllControls();
            }

            ( (CheckBox) findViewById( R.id.plan_exercise_when_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_when );
            ( (CheckBox) findViewById( R.id.plan_exercise_pastweek_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_pastweek );
            ( (CheckBox) findViewById( R.id.plan_exercise_where_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_where );
            ( (CheckBox) findViewById( R.id.plan_exercise_how_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_how );
            ( (CheckBox) findViewById( R.id.plan_exercise_often_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_often );
            ( (CheckBox) findViewById( R.id.plan_exercise_whom_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_whom );
            ( (CheckBox) findViewById( R.id.plan_exercise_interfere_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_interfere );
            ( (CheckBox) findViewById( R.id.plan_exercise_setbacks_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_setbacks );
            ( (CheckBox) findViewById( R.id.plan_exercise_situations_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_situations );
            ( (CheckBox) findViewById( R.id.plan_exercise_opportunities_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_opportunities );
            ( (CheckBox) findViewById( R.id.plan_exercise_lapses_cbx ) ).setChecked( as.intentionsAndPlans.plan_exercise_lapses );
            ( (CheckBox) findViewById( R.id.plan_intend_times_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_times );
            ( (CheckBox) findViewById( R.id.plan_intend_sweat_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_sweat );
            ( (CheckBox) findViewById( R.id.plan_intend_regularly_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_regularly );
            ( (CheckBox) findViewById( R.id.plan_intend_active_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_active );
            ( (CheckBox) findViewById( R.id.plan_intend_leisure_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_leisure );
            ( (CheckBox) findViewById( R.id.plan_intend_rehabilitation_cbx ) ).setChecked( as.intentionsAndPlans.plan_intend_rehabilitation );

        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( this, "An error occurred retrieving the application status", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onBackPressed()
    {
//        startActivity( new Intent( this, MainActivity.class ) );
        finish();
    }

}
