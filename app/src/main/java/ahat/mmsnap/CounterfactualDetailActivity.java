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

public class CounterfactualDetailActivity extends AppCompatActivity
{
    private String FILENAME;
    int itemId = -1;

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

        Bundle b = getIntent().getExtras();
        FILENAME = b.getString( "FILENAME" );

        if( null != b && b.containsKey( "itemId" ) )
        {
            itemId = b.getInt( "itemId" );
            try
            {
                JSONObject item = JSONArrayIOHandler.loadItem( getFilesDir().getPath() + "/" + FILENAME, itemId  );
                if( null == item )
                {
                    item = createEmptyItem();
                }
                ifText.setText( item.getString( "if" ) );
                thenText.setText( item.getString( "then" ) );
                activeSwitch.setChecked( item.getBoolean( "active" ) );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                showErrorSnackBar( "Could not load counterfactual thought" );
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
                    showErrorSnackBar( error );
                    return;
                }

                try
                {
                    JSONObject o = new JSONObject();
                    o.put( "id", String.valueOf( itemId ) );
                    o.put( "if", ifStatement );
                    o.put( "then", thenStatement );
                    o.put( "active", activeSwitch.isChecked() );

                    JSONArrayIOHandler.saveItem( getBaseContext(), o, getFilesDir().getPath() + "/" + FILENAME );
                }
                catch( Exception e )
                {
                    showErrorSnackBar( "Could not save counterfactual thought!" );
                }

                startActivity( new Intent( getBaseContext(), CounterfactualActivity.class ) );
            }
        } );
    }

    private JSONObject createEmptyItem()
    {
        JSONObject o = new JSONObject();
        try
        {
            o.put( "id", 0 );
            o.put( "if", "" );
            o.put( "then", "" );
            o.put( "active", true );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
        return o;
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
