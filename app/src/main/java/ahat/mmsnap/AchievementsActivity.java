package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ahat.mmsnap.models.DailyEvaluation;

public class AchievementsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_achievements );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.achievements_section_logo ) );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                onBackPressed();
            }
        } );


        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            final ArrayList<DailyEvaluation> evaluations = as.dailyEvaluations;

            ListView list = findViewById( R.id.achievements_list );
            AchievementsListAdapter adapter = new AchievementsListAdapter( this, evaluations, R.id.achievements_list );
            list.setAdapter( adapter );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not load daily evaluations", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }
    }

}
