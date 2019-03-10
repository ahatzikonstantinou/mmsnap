package ahat.mmsnap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

    private Button effButton;
    private Button hButton;
    private Button iButton;
    private Button pButton;
    private Button rButton;
    private Button wButton;

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
        effButton = findViewById( R.id.assessments_efficacy_btn );
        effButton.setOnClickListener( this );
        hButton = findViewById( R.id.assessments_health_btn );
        hButton.setOnClickListener( this );
        iButton = findViewById( R.id.assessments_illness_btn );
        iButton.setOnClickListener( this );
        pButton = findViewById( R.id.assessments_plans_btn );
        pButton.setOnClickListener( this );
        rButton = findViewById( R.id.assessments_risk_btn );
        rButton.setOnClickListener( this );
        wButton = findViewById( R.id.assessments_weekly_btn );
        wButton.setOnClickListener( this );

        applyLocalStatePolicy();
    }

    private void applyLocalStatePolicy()
    {
        try
        {
            ApplicationStatus as = ApplicationStatus.loadApplicationStatus( this );
            TextView messageView = findViewById( R.id.message );
            if( ApplicationStatus.NoInitialAssessments.NAME == as.getState().name() )
            {
                messageView.setText( "Please complete the initial assessments to proceed." );
                messageView.setVisibility( View.VISIBLE );

                Drawable done = getResources().getDrawable( R.drawable.ic_check_24dp, null );
                Drawable pending = getResources().getDrawable( android.R.drawable.ic_dialog_alert, null );
                Drawable arrow = getResources().getDrawable( R.drawable.subcategory_btn_img, null );
                effButton.setCompoundDrawablesWithIntrinsicBounds( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? done : pending, null, arrow, null );
                hButton.setCompoundDrawablesWithIntrinsicBounds( as.initialAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? done : pending, null, arrow, null );
                iButton.setCompoundDrawablesWithIntrinsicBounds( as.initialAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? done : pending, null, arrow, null );
                pButton.setCompoundDrawablesWithIntrinsicBounds( as.initialAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? done : pending, null, arrow, null );
                rButton.setCompoundDrawablesWithIntrinsicBounds( as.initialAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? done : pending, null, arrow, null );
            }
            else if( ApplicationStatus.NoFinalAssessments.NAME == as.getState().name() )
            {
                messageView.setText( "Please complete the final assessments to proceed." );
                messageView.setVisibility( View.VISIBLE );

                Drawable done = getResources().getDrawable( R.drawable.ic_check_24dp, null );
                Drawable pending = getResources().getDrawable( android.R.drawable.ic_dialog_alert, null );
                Drawable arrow = getResources().getDrawable( R.drawable.subcategory_btn_img, null );
                effButton.setCompoundDrawablesWithIntrinsicBounds( as.finalAssessmentsContain( ApplicationStatus.Assessment.SELF_EFFICACY ) ? done : pending, null, arrow, null );
                hButton.setCompoundDrawablesWithIntrinsicBounds( as.finalAssessmentsContain( ApplicationStatus.Assessment.SELF_RATED_HEALTH ) ? done : pending, null, arrow, null );
                iButton.setCompoundDrawablesWithIntrinsicBounds( as.finalAssessmentsContain( ApplicationStatus.Assessment.ILLNESS_PERCEPTION ) ? done : pending, null, arrow, null );
                pButton.setCompoundDrawablesWithIntrinsicBounds( as.finalAssessmentsContain( ApplicationStatus.Assessment.INTENTIONS ) ? done : pending, null, arrow, null );
                rButton.setCompoundDrawablesWithIntrinsicBounds( as.finalAssessmentsContain( ApplicationStatus.Assessment.HEALTH_RISK ) ? done : pending, null, arrow, null );
            }
            else
            {
                messageView.setVisibility( View.GONE );
                effButton.setVisibility( View.GONE );
                pButton.setVisibility( View.GONE );
                hButton.setVisibility( View.GONE );
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
