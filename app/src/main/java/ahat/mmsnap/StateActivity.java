package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StateActivity extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        applyStatePolicy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyStatePolicy();
    }

    private void applyStatePolicy()
    {
        try
        {
            ApplicationStatus status = ApplicationStatus.loadApplicationStatus( this );
            if( ApplicationStatus.State.NOT_LOGGED_IN == status.state )
            {
                startActivity( new Intent( this, LoginActivity.class ) );
            }
            else if( ApplicationStatus.State.NO_INITIAL_EVALUATIONS == status.state ||
                     ApplicationStatus.State.NO_FINAL_EVALUATIONS == status.state
            )
            {
                startActivity( new Intent( this, AssessmentsActivity.class ) );
            }
            else if( ApplicationStatus.State.WEEKLY_EVALUATION_PENDING == status.state )
            {
                startActivity( new Intent( this, WeeklyEvaluationActivity.class ) );
            }
        }
        catch( Exception e )
        {
            View view = findViewById( android.R.id.content );
            Snackbar.make( view, "Could not load the application status", Snackbar.LENGTH_LONG ).show();
        }
    }
}
