package ahat.mmsnap;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import ahat.mmsnap.JSON.ActionPlansStorage;
import ahat.mmsnap.JSON.JSONArrayIOHandler;

public class TodaysPlansActivity extends AppCompatActivity
{

    private JSONArray items;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_todays_plans );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle( R.string.title_activity_agents );
        getSupportActionBar().setSubtitle( R.string.title_activity_todays_plans );

        try
        {
            items = JSONArrayIOHandler.loadItems( getFilesDir().getPath() + "/" + ActionPlansStorage.FILENAME );

            final Calendar tc = Calendar.getInstance();
            tc.setTime( new Date() );
            for( int i = items.length() - 1 ; i >= 0 ; i-- )
            {
                final Calendar ic = IfThenDetailActivity.getCalendarFromYYYYMMDD( ( (JSONObject) items.get(i) ).getString( "date" ) );

                if( ( ic.get( Calendar.YEAR ) != tc.get( Calendar.YEAR ) ||
                      ic.get( Calendar.MONTH ) != tc.get( Calendar.MONTH ) ||
                      ic.get( Calendar.DAY_OF_MONTH ) != tc.get( Calendar.DAY_OF_MONTH ) ) ||
                    ( !( (JSONObject) items.get( i ) ).getBoolean( "active" ) )
                )
                {
                    items.remove( i );
                }
            }

            ListView list = findViewById( R.id.todays_listview );
            list.setAdapter( new TodaysListAdapter( this, items ) );

        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( R.id.todays_root_layout ), "Could not load today's plans", Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();

        }
    }

}
