package ahat.mmsnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
                        ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
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
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.DIET );
                            targetBehaviors.add( "Healthy Diet" );
                        }
                        c = findViewById( R.id.risk_alcohol_cbx );
                        if( c.isChecked() )
                        {
                            as.problematicBehaviors.add( ApplicationStatus.Behavior.ALCOHOL );
                            targetBehaviors.add( "Alcohol consumption" );
                        }

                        as.serverData.add( as.problematicBehaviors );
                        as.addAssessment( ApplicationStatus.Assessment.HEALTH_RISK );

                        AlertDialog alertDialog = new AlertDialog.Builder(HealthRiskActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Based on your answers your target Health Behaviors are " + android.text.TextUtils.join(", ", targetBehaviors ) + ". This application will focus on these behaviors for your plans and evaluations." );
                        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                               new DialogInterface.OnClickListener() {
                                                  public void onClick( DialogInterface dialog, int which) {
                                                      dialog.dismiss();

                                                      finish();
                                                      Intent intent = new Intent( HealthRiskActivity.this, MainActivity.class );
                                                      Bundle b = new Bundle();
                                                      b.putSerializable( "display", MainActivity.Display.SECTIONS );
                                                      intent.putExtras( b );
                                                      startActivity( intent );

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

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );

            ( (CheckBox) findViewById( R.id.risk_smoke_cbx ) ).setChecked( as.problematicBehaviors.contains( ApplicationStatus.Behavior.SMOKING ) );
            ( (CheckBox) findViewById( R.id.risk_exercise_cbx ) ).setChecked( as.problematicBehaviors.contains( ApplicationStatus.Behavior.ACTIVITY ) );
            ( (CheckBox) findViewById( R.id.risk_food_cbx ) ).setChecked( as.problematicBehaviors.contains( ApplicationStatus.Behavior.DIET ) );
            ( (CheckBox) findViewById( R.id.risk_alcohol_cbx ) ).setChecked( as.problematicBehaviors.contains( ApplicationStatus.Behavior.ALCOHOL ) );
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
