package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ahat.mmsnap.models.WeeklyEvaluation;

public class WeeklyEvaluationsListActivity extends AppCompatActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_weekly_evaluations_list );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                onBackPressed();
            }
        } );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo, null ) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_weekly_evaluations_list );

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            final ArrayList<WeeklyEvaluation> evaluations = as.weeklyEvaluations;

            ListView list = findViewById( R.id.weekly_evaluations_list );
            WeeklyEvaluationsListAdapter adapter = new WeeklyEvaluationsListAdapter( this, evaluations, R.id.weekly_evaluations_list );
            list.setAdapter( adapter );
            list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
                                             {
                                                 Intent intent = new Intent( getBaseContext(), WeeklyEvaluationActivity.class );
                                                 intent.putExtra( "evaluation", evaluations.get( i ) );
                                                 startActivity( intent );
                                             }
                                         }
            );

            findViewById( R.id.message_layout ).setVisibility( as.pendingWeeklyEvaluationsExist() ? View.VISIBLE : View.GONE );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not load weekly evaluations", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }
    }

    @Override
    public void onBackPressed()
    {
//        startActivity( new Intent( this, MainActivity.class ) );
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable( "display", MainActivity.Display.SECTIONS );
        intent.putExtras( b );
        startActivity( intent );
    }

}
