package ahat.mmsnap;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.CopingPlansStorage;
import ahat.mmsnap.JSON.JSONArrayConverterActionPlan;
import ahat.mmsnap.JSON.JSONArrayConverterCopingPlan;
import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.CopingPlan;
import ahat.mmsnap.Models.IfThenPlan;

public class DailyEvaluationsListActivity extends AppCompatActivity
{

    private ArrayList<IfThenPlan> items;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_daily_evaluations_list );
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
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo, null ) );
        getSupportActionBar().setTitle( R.string.title_activity_if_then );
        getSupportActionBar().setSubtitle( R.string.title_activity_daily_evaluations_list );

        try
        {
            items = new ArrayList<>();

            //load all action plans
            ActionPlansStorage aps = new ActionPlansStorage( this );
            JSONArrayConverterActionPlan jacap = new JSONArrayConverterActionPlan();
            aps.read( jacap );
            for( int i = 0 ; i < jacap.getActionPlans().size() ; i++ )
            {
                ActionPlan p = jacap.getActionPlans().get( i );
                if( p.needsEvaluation() )
                {
                    items.add( p );
                }
            }

            //load all coping plans
            CopingPlansStorage cps = new CopingPlansStorage( this );
            JSONArrayConverterCopingPlan jaccp = new JSONArrayConverterCopingPlan();
            cps.read( jaccp );
            for( int i = 0 ; i < jaccp.getCopingPlans().size() ; i++ )
            {
                CopingPlan p = jaccp.getCopingPlans().get( i );
                if( p.needsEvaluation() )
                {
                    items.add( p );
                }
            }

            ListView list = findViewById( R.id.daily_evaluations_list );
            IfThenListAdapter adapter = new IfThenListAdapter( this, items, false );
            list.setAdapter( adapter );
            list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
                                             {
                                                 Intent intent;
                                                 IfThenPlan item = items.get( i );
                                                 if( item instanceof ActionPlan )
                                                 {
                                                     intent = new Intent( getBaseContext(), ActionPlansDetailActivity.class );
                                                     intent.putExtra( "action_plan", item );
                                                 }
                                                 else
                                                 {
                                                     intent = new Intent( getBaseContext(), CopingPlansDetailActivity.class );
                                                     intent.putExtra( "coping_plan", item );
                                                 }
                                                 intent.putExtra( "evaluation_mode", true );
                                                 startActivity( intent );
                                             }
                                         }
            );

            findViewById( R.id.message ).setVisibility( items.size() > 0 ? View.VISIBLE : View.GONE );
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

    @Override
    public void onBackPressed()
    {
        startActivity( new Intent( this, IfThenActivity.class ) );
    }

}
