package ahat.mmsnap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static ahat.mmsnap.CounterfactualActivity.FILENAME;

public class CounterfactualDetailActivity extends AppCompatActivity
{

    int itemId = 0;
    JSONArray items;
    private Switch activeSwitch;
    private EditText thenText;
    private EditText ifText;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_counterfactual_detail );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_if_then );
        getSupportActionBar().setSubtitle( R.string.title_activity_counterfactual );

        ifText = (EditText) findViewById( R.id.counterfactual_detail_if_statement );
        thenText = (EditText) findViewById( R.id.counterfactual_detail_then_statement );
        activeSwitch = (Switch) findViewById( R.id.counterfactual_detail_switch );

        items = CounterfactualActivity.loadItems( this, findViewById( R.id.counterfactual_detail_main_layout ), "Could not load counterfactual thoughts." );

        itemId = items.length();

        Bundle b = getIntent().getExtras();
        if( null != b && b.containsKey( "itemId" ) )
        {
            itemId = b.getInt( "itemId" );
            if( itemId > items.length() - 1 )
            {
//                Toast.makeText( this, "The requsted counterfactual thought was not found. Create a new one or retry.", Toast.LENGTH_LONG ).show();
                showErrorSnackBar( "The requested counterfactual thought was not found. Create a new one or retry." );
            }
            else
            {
                try
                {
                    ifText.setText( ( (JSONObject) items.get( itemId ) ).getString( "if" ) );
                    thenText.setText( ( (JSONObject) items.get( itemId ) ).getString( "then" ) );
                    activeSwitch.setChecked( ( (JSONObject) items.get( itemId ) ).getBoolean( "active" ) );
                }
                catch( JSONException e )
                {
                    e.printStackTrace();
//                    Toast.makeText( this, "Could not load counterfactual thought", Toast.LENGTH_LONG ).show();
                    showErrorSnackBar( "Could not load counterfactual thought" );
                }
            }
        }

        Button save = (Button) findViewById( R.id.counterfactual_detail_save );
        save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                String ifStatement = ifText.getText().toString().trim();
                String thenStatement = thenText.getText().toString().trim();
                String error = "";
                if( 0 == ifStatement.length() )
                {
                    error = " enter an if statement";
                }
                if( 0 == thenStatement.length() )
                {
                    error += ( error.length() > 0 ? " and " : "enter " ) + " a then statement ";
                }
                if( 0 < error.length() )
                {
                    error += "Please ";
                    Toast.makeText( CounterfactualDetailActivity.this, error, Toast.LENGTH_LONG ).show();
                    showErrorSnackBar( error );
                    return;
                }

                try
                {
                    JSONObject o = new JSONObject();
                    if( itemId < items.length() )
                    {
                        o = (JSONObject) items.get( itemId );
                    }
                    o.put( "id", String.valueOf( itemId ) );
                    o.put( "if", ifStatement );
                    o.put( "then", thenStatement );
                    o.put( "active", activeSwitch.isChecked() );

                    if( itemId == items.length() )
                    {
                        items.put( o );
                    }

                    saveItems( getBaseContext(), items );
                }
                catch( Exception e )
                {
                    showErrorSnackBar( "Could not save counterfactual thought! File " + FILENAME + " was not found." );
                }

                startActivity( new Intent( getBaseContext(), CounterfactualActivity.class ) );
            }
        } );
    }


    public static void saveItems( Context context, JSONArray items ) throws IOException
    {
        String filePath = context.getFilesDir().getPath().toString() + "/" + FILENAME;
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( FILENAME, Context.MODE_PRIVATE );
        fos.write( items.toString().getBytes() );
        fos.close();
    }

    protected void showErrorSnackBar( String message )
    {
        View view = findViewById( R.id.counterfactual_detail_main_layout );
        Snackbar.make( view, message, Snackbar.LENGTH_INDEFINITE )
                .setAction( "DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity( getParentActivityIntent() );
                    }
                } ).show();

    }

}
