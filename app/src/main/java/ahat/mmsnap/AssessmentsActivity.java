package ahat.mmsnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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

                break;
            case R.id.assessments_illness_btn:
                break;
            case R.id.assessments_plans_btn:
                intent = new Intent( this, PlansActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_risk_btn:
                intent = new Intent( this, HealthRiskActivity.class);
                startActivity( intent );
                break;
            default:
                break;
        }
    }
}
