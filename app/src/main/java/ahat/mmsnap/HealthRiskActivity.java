package ahat.mmsnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
                        ArrayList<String> targetBehaviors = new ArrayList<>( 4 );
                        ApplicationStatus as = ApplicationStatus.loadApplicationStatus( view.getContext() );
                        as.problematicBehaviors = new ArrayList<>( 4 );
                        CheckBox c;
                        c = findViewById( R.id.risk_smoke_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.SMOKING );
                            targetBehaviors.add( "Smoking" );
                        }
                        c = findViewById( R.id.risk_exercise_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.ACTIVITY );
                            targetBehaviors.add( "Physical Activity" );
                        }
                        c = findViewById( R.id.risk_food_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.EATING );
                            targetBehaviors.add( "Healthy Diet" );
                        }
                        c = findViewById( R.id.risk_alcohol_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.ALCOHOL );
                            targetBehaviors.add( "Alcohol consumption" );
                        }

                        as.addAssessment( ApplicationStatus.Assessment.HEALTH_RISK );

                        //TODO SEND_TO_SERVER

                        AlertDialog alertDialog = new AlertDialog.Builder(HealthRiskActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Based on your answers your target Health Behaviors are " + android.text.TextUtils.join(", ", targetBehaviors ) + ". This application will focus on these behaviors for your plans and evaluations." );
                        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                               new DialogInterface.OnClickListener() {
                                                  public void onClick( DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                      startActivity( getParentActivityIntent() );
                                                  }
                                              });
                        alertDialog.show();

                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                        Toast.makeText( HealthRiskActivity.this, "An error occurred while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        );

    }

}
