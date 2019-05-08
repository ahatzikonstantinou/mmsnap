package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class EQVASActivity extends AppCompatActivity
{

    private SeekBar mSeekBar;
    private EditText mEditText;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_eqvas );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_eqvas );



        mEditText = (EditText) findViewById( R.id.eqvas_editText );
        mEditText.setFilters(new InputFilter[]{ new InputFilterMinMax( "0", "100")});

        mSeekBar=(SeekBar)findViewById( R.id.eqvas_seekBar);
        // perform seek bar change listener event used for getting the progress value
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                if( fromUser )
                {
                    mEditText.setText( Integer.toString( progressChangedValue ) );
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged( Editable s) {
                try{
                    //Update Seekbar value after entering a number
                    mSeekBar.setProgress(Integer.parseInt(s.toString()));
                } catch(Exception ex) {}
            }
        });

        findViewById( R.id.eqvas_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                try
                {
                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );
                    as.eqvas = Integer.parseInt( mEditText.getText().toString() );
                    as.serverData.add( as.eqvas );
                    as.addAssessment( ApplicationStatus.Assessment.ILLNESS_PERCEPTION );

                    finish();
                    Intent intent = new Intent( EQVASActivity.this, MainActivity.class );
                    Bundle b = new Bundle();
                    b.putSerializable( "display", MainActivity.Display.SECTIONS );
                    intent.putExtras( b );
                    startActivity( intent );

                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( EQVASActivity.this, "An error occured while saving your answer. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );


        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            mEditText.setText( String.valueOf( as.eqvas ) );
            mSeekBar.setProgress( as.eqvas );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( this, "An error occurred retrieving the application status", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onBackPressed()
    {
//        startActivity( new Intent( this, MainActivity.class ) );
        finish();
    }

}
