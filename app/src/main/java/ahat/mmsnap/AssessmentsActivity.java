package ahat.mmsnap;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentsActivity extends AppCompatActivity implements View.OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_assessments );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );

        //buttons events
        Button effButton = ( Button ) findViewById( R.id.assessments_efficacy_btn );
        effButton.setOnClickListener( this );
        Button hButton = ( Button ) findViewById( R.id.assessments_health_btn );
        hButton.setOnClickListener( this );
        Button iButton = ( Button ) findViewById( R.id.assessments_illness_btn );
        iButton.setOnClickListener( this );
        Button pButton = ( Button ) findViewById( R.id.assessments_plans_btn );
        pButton.setOnClickListener( this );
        Button rButton = ( Button ) findViewById( R.id.assessments_risk_btn );
        rButton.setOnClickListener( this );
        Button wButton = ( Button ) findViewById( R.id.assessments_weekly_btn );
        wButton.setOnClickListener( this );

        applyLocalStatePolicy();
    }

    private void applyLocalStatePolicy()
    {
        try
        {
            ApplicationStatus as = ApplicationStatus.loadApplicationStatus( this );
            TextView messageView = findViewById( R.id.message );
            if( ApplicationStatus.State.NO_INITIAL_EVALUATIONS == as.state )
            {
                messageView.setText( "Please complete the initial assessments to proceed." );
                messageView.setVisibility( View.VISIBLE );
            }
            else if( ApplicationStatus.State.NO_FINAL_EVALUATIONS == as.state )
            {
                messageView.setText( "Please complete the final assessments to proceed." );
                messageView.setVisibility( View.VISIBLE );
            }
            else
            {
                messageView.setVisibility( View.GONE );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( this, "An error occurred retrieving the application status", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        applyLocalStatePolicy();
    }

    @Override
    public void onClick( View view )
    {
        Intent intent;
        switch (view.getId()){
            case R.id.assessments_efficacy_btn:
                intent = new Intent( this, EfficacyActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_health_btn:
                intent = new Intent( this, SelfRatedHealthActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_illness_btn:
                intent = new Intent( this, EQVASActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_plans_btn:
                intent = new Intent( this, PlansActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_risk_btn:
                intent = new Intent( this, HealthRiskActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_weekly_btn:
                intent = new Intent( this, WeeklyEvaluationActivity.class);
                startActivity( intent );
                break;
            default:
                break;
        }
    }
}
