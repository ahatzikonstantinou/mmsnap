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
            ApplicationStatus status = ApplicationStatus.getInstance( this );
            if( ApplicationStatus.NotLoggedIn.NAME == status.getState().name() )
            {
                if( 0 != getIntent().getComponent().getClassName().compareTo( LoginActivity.class.getName() ) )    // ahat: == does not work here, I don't know why
                {
                    startActivity( new Intent( this, LoginActivity.class ) );
                }
            }
            else if( ApplicationStatus.NoInitialAssessments.NAME == status.getState().name() ||
                     ApplicationStatus.NoFinalAssessments.NAME == status.getState().name()
            )
            {
                if( 0 != getIntent().getComponent().getClassName().compareTo( AssessmentsActivity.class.getName() ) &&    // ahat: == does not work here, I don't know why
                    0 != getIntent().getComponent().getClassName().compareTo( MainActivity.class.getName() )
                )
                {
                    startActivity( new Intent( this, AssessmentsActivity.class ) );
                }
            }
            else if( status.pendingWeeklyEvaluationsExist() )
            {
                if( 0 != getIntent().getComponent().getClassName().compareTo( WeeklyEvaluationsListActivity.class.getName() ) )    // ahat: == does not work here, I don't know why
                {
                    startActivity( new Intent( this, WeeklyEvaluationsListActivity.class ) );
                }
            }
            else if( status.pendingDailyEvaluationsExist() )
            {
                if( 0 != getIntent().getComponent().getClassName().compareTo( DailyEvaluationsListActivity.class.getName() ) )    // ahat: == does not work here, I don't know why
                {
                    startActivity( new Intent( this, DailyEvaluationsListActivity.class ) );
                }
            }
        }
        catch( Exception e )
        {
            View view = findViewById( android.R.id.content );
            Snackbar.make( view, "Could not load the application status", Snackbar.LENGTH_LONG ).show();
        }
    }
}
