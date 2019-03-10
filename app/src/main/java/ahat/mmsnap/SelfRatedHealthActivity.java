package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class SelfRatedHealthActivity extends AppCompatActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_self_rated_health );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_self_rated_health );


        findViewById( R.id.self_rated_health_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                try
                {
                    ApplicationStatus as = ApplicationStatus.loadApplicationStatus( view.getContext() );

                    //TODO Add self rated health answers to Application status

                    as.addAssessment( ApplicationStatus.Assessment.SELF_RATED_HEALTH );

                    //TODO SEND_TO_SERVER

                    startActivity( getParentActivityIntent() );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( SelfRatedHealthActivity.this, "An error occurred while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

}
