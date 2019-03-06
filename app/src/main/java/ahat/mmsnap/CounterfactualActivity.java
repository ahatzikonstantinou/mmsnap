package ahat.mmsnap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class CounterfactualActivity extends AppCompatActivity
{

    public static final String FILENAME = "counterfactual.json";
    private boolean delete;
    private ListView list;
    private FloatingActionButton fab;
    private JSONArray items;
    private CounterfactualListAdapter adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_counterfactual );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        delete = false;
//        Intent intent = getIntent();
//        delete = intent.getBooleanExtra( "delete", false );

        fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                if( delete )
                {
                    deleteItems( findViewById( R.id.counterfactual_main_layout ) );
                }
                else
                {
                    startActivity( new Intent( getBaseContext(), CounterfactualDetailActivity.class ) );
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_if_then );
        getSupportActionBar().setSubtitle( R.string.title_activity_counterfactual );

//        if( delete )
//        {
//            fab.setImageResource( android.R.drawable.ic_menu_delete );
//        }
//        else
//        {
//            fab.setImageResource( android.R.drawable.ic_menu_delete );
//        }


        items = loadItems( this, findViewById( R.id.counterfactual_main_layout ), "Counterfactual thoughts could not be loaded.");
        list = (ListView) findViewById( R.id.counterfactual_list );
        adapter = new CounterfactualListAdapter( this, items, delete );
        list.setAdapter( adapter );
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
            {
                if( delete )
                {
                    if( adapter.deleteIndex.contains( i ) )
                    {
                        adapter.deleteIndex.remove( (Integer) i );
                        Log.d( "Ahat:", "Removing item " + String.valueOf( i ) + " from deleteIndex" );
                    }
                    else
                    {
                        adapter.deleteIndex.add( i );
                        Log.d( "Ahat:", "Adding item " + String.valueOf( i ) + " to deleteIndex" );
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Intent intent = new Intent( getBaseContext(), CounterfactualDetailActivity.class );
                    Bundle b = new Bundle();
                    b.putInt( "itemId", i );
                    intent.putExtras( b );
                    startActivity( intent );
                }
            }
        } );

        list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView<?> adapterView, View view, int i, long l )
            {
                if( !delete )
                {
                    fab.setImageResource( android.R.drawable.ic_menu_delete );
                    adapter.deleteAction = true;
                    adapter.deleteIndex.add( i );
                    delete = true;
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    fab.setImageResource( R.drawable.ic_add_white_24dp );
                    adapter.deleteAction = false;
                    adapter.deleteIndex.clear();
                    delete = false;
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        } );

    }

    @Override
    public void onBackPressed()
    {
        if( delete )
        {
            fab.setImageResource( R.drawable.ic_add_white_24dp );
            adapter.deleteAction = false;
            adapter.deleteIndex.clear();
            delete = false;
            adapter.notifyDataSetChanged();
        }
        else
        {
            startActivity( getParentActivityIntent() );
        }
    }

    private void deleteItems( View view )
    {
        String errorMessage = "Could not delete selected counterfactual thoughts";
        try
        {
            for( int i = items.length() ; i >= 0  ; i-- )
            {
                if( adapter.deleteIndex.contains( i ) )
                {
                    items.remove( i );
                }
            }

            CounterfactualDetailActivity.saveItems( getBaseContext(), items );
            delete = false;
            fab.setImageResource( R.drawable.ic_add_white_24dp );
            adapter.deleteAction = false;
            adapter.deleteIndex.clear();
            adapter.notifyDataSetChanged();
        }
        catch( Exception e )
        {
            Snackbar.make( view, errorMessage, Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }

//        startActivity( new Intent( getBaseContext(), CounterfactualActivity.class ) );
    }

    public static JSONArray loadItems( final Activity activity, View view, String errorMessage )
    {
        JSONArray items = new JSONArray();

        String filePath = activity.getFilesDir().getPath().toString() + "/" + FILENAME;
        File file = new File( filePath );
        if( !file.exists() )
        {
            return items;
        }

        try
        {
            FileInputStream is = new FileInputStream( file);

            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try
            {
                Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
                int n;
                while( ( n = reader.read( buffer ) ) != -1 )
                {
                    writer.write( buffer, 0, n );
                }

                String jsonString = writer.toString();
                items = new JSONArray( jsonString  );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                Snackbar.make( view, errorMessage, Snackbar.LENGTH_INDEFINITE )
                        .setAction( "Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.startActivity( activity.getIntent() );
                            }
                        } ).show();
            }
            finally
            {
                is.close();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return items;
    }

}
