package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;

public class HealthRiskActivity extends AppCompatActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_health_risk );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_health_risk );

        findViewById( R.id.risk_submit_btn ).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick( View view )
                {
                    try
                    {
                        ApplicationStatus as = ApplicationStatus.loadApplicationStatus( view.getContext() );
                        as.problematicBehaviors = new ArrayList<>( 4 );
                        CheckBox c;
                        c = findViewById( R.id.risk_smoke_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.SMOKING );
                        }
                        c = findViewById( R.id.risk_exercise_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.ACTIVITY );
                        }
                        c = findViewById( R.id.risk_food_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.EATING );
                        }
                        c = findViewById( R.id.risk_alcohol_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.ALCOHOL );
                        }
                        as.saveApplicationStatus( view.getContext() );

                        //TODO SEND_TO_SERVER
                        startActivity( getParentActivityIntent() );
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                        Toast.makeText( HealthRiskActivity.this, "An error occured while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        );

    }

}
