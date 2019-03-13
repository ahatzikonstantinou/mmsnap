package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CounterfactualDetailActivity extends AppCompatActivity
{
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

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );

            ifText.setText( as.counterfactualThought.ifStatement );
            thenText.setText( as.counterfactualThought.thenStatement );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            showErrorSnackBar( "Could not load counterfactual thought" );
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
                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
                    as.counterfactualThought.ifStatement = ifStatement;
                    as.counterfactualThought.thenStatement = thenStatement;
                    as.save();
                    startActivity( new Intent( CounterfactualDetailActivity.this, IfThenActivity.class ) );
                }
                catch( Exception e )
                {
                    showErrorSnackBar( "Could not save counterfactual thought!" );
                }
            }
        } );
    }

    protected void showErrorSnackBar( String message )
    {
        View view = findViewById( R.id.counterfactual_detail_main_layout );
        Snackbar.make( view, message, Snackbar.LENGTH_INDEFINITE )
                .setAction( "RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity( getIntent() );
                    }
                } ).show();

    }

}
