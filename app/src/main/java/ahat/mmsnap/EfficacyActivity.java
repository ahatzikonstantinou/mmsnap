package ahat.mmsnap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class EfficacyActivity extends AppCompatActivity
{

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
                    //TODO send to server
                    ApplicationStatus as = ApplicationStatus.loadApplicationStatus( view.getContext() );
                    as.selfEfficacy.lifestyle = ( ( CheckBox ) findViewById( R.id.efficacy_lifestyle_cbx ) ).isChecked();
                    as.selfEfficacy.weekly_goals = ( ( CheckBox ) findViewById( R.id.efficacy_goals_cbx ) ).isChecked();
                    as.selfEfficacy.multimorbidity = ( ( CheckBox ) findViewById( R.id.efficacy_mm_cbx ) ).isChecked();

                    as.addAssessment( ApplicationStatus.Assessment.SELF_EFFICACY );

                    //TODO SEND_TO_SERVER
                    startActivity( getParentActivityIntent() );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( EfficacyActivity.this, "An error occured while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

}
