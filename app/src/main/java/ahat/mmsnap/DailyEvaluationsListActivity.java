package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ahat.mmsnap.json.DailyEvaluationsStorage;
import ahat.mmsnap.json.JSONArrayConverterDailyEvaluation;
import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.DailyEvaluation;

public class DailyEvaluationsListActivity extends AppCompatActivity
{

    private ArrayList<DailyEvaluation> items;
    private boolean showPendingOnly = false;
    private DailyEvaluationListAdapter adapter;
    private Menu menu;

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

            //load all evaluation
            items = loadEvaluations();
            Collections.sort( items, new Comparator<DailyEvaluation>()
            {
                @Override
                public int compare( DailyEvaluation e1, DailyEvaluation e2 )
                {
                    return ( e1.plan.year - e2.plan.year )*52 + ( e1.plan.weekOfYear - e2.plan.weekOfYear )*7 + e1.getWeekDay().ordinal() - e2.getWeekDay().ordinal();
                }
            } );


            ListView list = findViewById( R.id.daily_evaluations_list );
            adapter = new DailyEvaluationListAdapter( this, items, false );
            list.setAdapter( adapter );
            list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
                                             {
                                                 Intent intent = new Intent( getBaseContext(), DailyEvaluationsDetailActivity.class );
                                                 intent.putExtra( "showPendingOnly", showPendingOnly );
                                                 intent.putExtra( "evaluation", items.get( i ) );
                                                 startActivity( intent );
                                             }
                                         }
            );

            if( pendingExist( items ) )
            {
                showPendingOnly = true;
                filterPendingOnly( true );
            }

            findViewById( R.id.message_layout ).setVisibility( pendingExist( items ) ? View.VISIBLE : View.GONE );
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

    private ArrayList<DailyEvaluation> loadEvaluations() throws IOException, JSONException, ConversionException
    {
        DailyEvaluationsStorage storage = new DailyEvaluationsStorage( this );
        JSONArrayConverterDailyEvaluation jac = new JSONArrayConverterDailyEvaluation();
        storage.read( jac );
        return jac.getDailyEvaluations();
    }

    private boolean pendingExist( ArrayList<DailyEvaluation> items )
    {
        for( DailyEvaluation evaluation : items )
        {
            if( !evaluation.isEvaluated() )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable( "display", MainActivity.Display.SECTIONS );
        intent.putExtras( b );
        startActivity( intent );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean( "showPendingOnly", showPendingOnly );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState( savedInstanceState );

        showPendingOnly = savedInstanceState.getBoolean( "showPendingOnly" );
        filterPendingOnly( showPendingOnly );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu)
    {
        this.menu = menu;
        getMenuInflater().inflate( R.menu.daily_evaluations, menu);

        if( showPendingOnly )
        {
            menu.getItem(0).setIcon( R.drawable.filter_pending_on );
            menu.getItem(0).setChecked( true );
        }
        else
        {
            menu.getItem(0).setIcon( R.drawable.filter_pending_off );
            menu.getItem(0).setChecked( false );
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch (item.getItemId())
        {
            case R.id.action_filter_pending:
                showPendingOnly = !showPendingOnly;
                item.setChecked( showPendingOnly );

                // android documentation says icon setting must be done manually ...
                // http://developer.android.com/guide/topics/ui/menus.html
                item.setIcon( item.isChecked() ? R.drawable.filter_pending_on : R.drawable.filter_pending_off );

                filterPendingOnly( showPendingOnly );
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterPendingOnly( boolean filter )
    {
        try
        {
            ArrayList<DailyEvaluation> all = loadEvaluations();
            Collections.sort( all, new Comparator<DailyEvaluation>()
            {
                @Override
                public int compare( DailyEvaluation e1, DailyEvaluation e2 )
                {
                    return ( e1.plan.year - e2.plan.year )*52 + ( e1.plan.weekOfYear - e2.plan.weekOfYear )*7 + e1.getWeekDay().ordinal() - e2.getWeekDay().ordinal();
                }
            } );

            if( !filter )
            {
                items = all;
            }
            else
            {
                ArrayList<DailyEvaluation> evaluations = new ArrayList<>();
                for( DailyEvaluation evaluation : all )
                {
                    if( !evaluation.isEvaluated() )
                    {
                        evaluations.add( evaluation );
                    }
                }
                items = evaluations;
            }
            adapter.items = items;
            adapter.notifyDataSetInvalidated();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( android.R.id.content ), "Could not load daily evaluations", Snackbar.LENGTH_SHORT ).show();
        }

    }
}
