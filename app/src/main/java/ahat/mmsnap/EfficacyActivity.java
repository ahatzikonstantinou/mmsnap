package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class EfficacyActivity extends MassDisableActivity // AppCompatActivity
{

    protected int getActivityResLayout(){ return R.layout.activity_efficacy; }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_efficacy );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_efficacy );

        findViewById( R.id.efficacy_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                try
                {
                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
                    as.selfEfficacy.lifestyle = ( ( CheckBox ) findViewById( R.id.efficacy_lifestyle_cbx ) ).isChecked();
                    as.selfEfficacy.weekly_goals = ( ( CheckBox ) findViewById( R.id.efficacy_goals_cbx ) ).isChecked();
                    as.selfEfficacy.multimorbidity = ( ( CheckBox ) findViewById( R.id.efficacy_mm_cbx ) ).isChecked();

                    as.serverData.add( as.selfEfficacy );
                    as.addAssessment( ApplicationStatus.Assessment.SELF_EFFICACY );

                    finish();
                    Intent intent = new Intent( EfficacyActivity.this, MainActivity.class );
                    Bundle b = new Bundle();
                    b.putSerializable( "display", MainActivity.Display.SECTIONS );
                    intent.putExtras( b );
                    startActivity( intent );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( EfficacyActivity.this, "An error occured while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
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
                findViewById( R.id.efficacy_submit_btn ).setVisibility( View.GONE );
                disableAllControls();
            }

            ( ( CheckBox ) findViewById( R.id.efficacy_lifestyle_cbx ) ).setChecked( as.selfEfficacy.lifestyle );
            ( ( CheckBox ) findViewById( R.id.efficacy_goals_cbx ) ).setChecked( as.selfEfficacy.weekly_goals );
            ( ( CheckBox ) findViewById( R.id.efficacy_mm_cbx ) ).setChecked( as.selfEfficacy.multimorbidity );
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
