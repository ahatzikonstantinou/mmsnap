package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class PlansActivity extends AppCompatActivity
{

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
                    //TODO send to server
                    ApplicationStatus as = ApplicationStatus.loadApplicationStatus( view.getContext() );

                    //TODO Add to Application status

                    if( ApplicationStatus.State.NO_INITIAL_EVALUATIONS == as.state &&
                        !as.initialAssessments.contains( ApplicationStatus.Assessment.INTENTIONS )
                    )
                    {
                        as.initialAssessments.add( ApplicationStatus.Assessment.INTENTIONS );
                    }
                    if( ApplicationStatus.State.NO_FINAL_EVALUATIONS == as.state &&
                        !as.finalAssessments.contains( ApplicationStatus.Assessment.INTENTIONS )
                    )
                    {
                        as.finalAssessments.add( ApplicationStatus.Assessment.INTENTIONS );
                    }
                    as.saveApplicationStatus( view.getContext() );
                    startActivity( getParentActivityIntent() );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( PlansActivity.this, "An error occured while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

}
