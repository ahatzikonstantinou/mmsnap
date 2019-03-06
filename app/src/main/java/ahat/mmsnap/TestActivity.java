package ahat.mmsnap;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

public class TestActivity extends AppCompatActivity
{

    ProgressBar    mProgressBar;
    TextView       mProgressTextView;
    Button         mPlayButton;
    Button         if1Button;
    Button         if2Button;
    TextView       thenStatementTextView;
    TextView       ifStatementTextView;
    LinearLayout   ifLayout;
    ImageView      correctImageView;
    ImageView      errorImageView;
    int            lapsedProgressTicks = 0;
    boolean        if1Right;
    boolean        if1Chosen;
    CountDownTimer mCountDownTimer;
    boolean        running;
    boolean        finished;
    boolean        timedOut;
    int            timeLapsed;
    int            testIndex;
    JSONObject     test;
    int            TOTAL_MILLIS        = 20000;
    int            INTERVAL_MILLIS     = 1000;
    int            PROGRESS_STEPS      = TOTAL_MILLIS / INTERVAL_MILLIS;


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_test );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setTitle( R.string.title_activity_edu);
        getSupportActionBar().setSubtitle( R.string.title_activity_test);
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.edu_section_logo) );

        mProgressBar = (ProgressBar) findViewById( R.id.edu_test_progressBar );
        mProgressTextView = (TextView) findViewById( R.id.edu_test_progress );
        mPlayButton = (Button) findViewById( R.id.edu_test_play_btn );
        if1Button = (Button) findViewById( R.id.edu_test_if1_btn );
        if2Button = (Button) findViewById( R.id.edu_test_if2_btn );
        thenStatementTextView = (TextView) findViewById( R.id.edu_test_then_statement );
        ifStatementTextView = (TextView) findViewById( R.id.edu_test_if_statement );
        ifLayout = (LinearLayout) findViewById( R.id.edu_test_if_layout );
        correctImageView = (ImageView) findViewById( R.id.edu_test_correct );
        errorImageView = (ImageView) findViewById( R.id.edu_test_error );

        TextView prompt = (TextView) findViewById( R.id.edu_test_prompt );
        prompt.setText( getString( R.string.edu_test_prompt, PROGRESS_STEPS ) );

        mPlayButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                mPlayButton.setVisibility( View.INVISIBLE );
                loadTest();
                timeLapsed = 0;
                lapsedProgressTicks = 0;
                startTimer( 0 );
            }
        } );

        if1Button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if( finished )
                {
                    return;
                }
                running = false;
                finished = true;
                if1Chosen = true;
                timedOut = false;
                setResult( if1Button, if1Right );
            }
        } );

        if2Button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if( finished )
                {
                    return;
                }
                running = false;
                finished = true;
                if1Chosen = false;
                timedOut = false;
                setResult( if2Button, !if1Right );
            }
        } );

        finished = false;
        running = false;

        mProgressTextView.setText( Integer.toString( PROGRESS_STEPS ) );

//        loadTest();
//        startTimer( (long) 0 );

    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState(outState);
        outState.putInt( "timeLapsed", timeLapsed );
        outState.putBoolean( "finished", finished );
        outState.putBoolean( "running", running );
        outState.putBoolean( "timedOut", timedOut );
        if( null != test )
        {
            outState.putString( "test", test.toString() );
        }
        outState.putBoolean( "if1Right", if1Right );
        outState.putInt( "lapsedProgressTicks", lapsedProgressTicks );
        if( finished )
        {
            outState.putBoolean( "if1Chosen", if1Chosen );
        }
        if( null != mCountDownTimer )
        {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState( savedInstanceState );
        timeLapsed = savedInstanceState.getInt( "timeLapsed" );
        finished = savedInstanceState.getBoolean( "finished" );
        running = savedInstanceState.getBoolean( "running" );
        timedOut = savedInstanceState.getBoolean( "timedOut" );
        if1Right = savedInstanceState.getBoolean( "if1Right" );
        lapsedProgressTicks = savedInstanceState.getInt( "lapsedProgressTicks" );
        if( savedInstanceState.containsKey( "test" ) )
        {
            try
            {
                test = new JSONObject( savedInstanceState.getString( "test" ) );
                updateTestUI();
            }
            catch( JSONException e )
            {
                e.printStackTrace();
            }
        }
        if( finished )
        {
            if1Chosen = savedInstanceState.getBoolean( "if1Chosen" );
            setResult( if1Chosen ? if1Button : if2Button, ( if1Right && if1Chosen ) || ( !if1Right && !if1Chosen ) );
        }
        if( !finished && running )
        {
            startTimer( timeLapsed );
        }
    }

    protected void setResult( Button choiceButton, boolean correct )
    {
        running = false;

        if( null != mCountDownTimer )
        {
            mCountDownTimer.cancel();
        }
        if( timedOut )
        {
            ifStatementTextView.setText( "" );
        }
        else
        {
            ifStatementTextView.setText( choiceButton.getText() );
        }
        ifLayout.setBackground( getResources().getDrawable( R.drawable.if_then_border ) );
        mProgressBar.setVisibility( View.GONE );
        mProgressTextView.setVisibility( View.GONE );
        mPlayButton.setVisibility( View.VISIBLE );
        if( correct && !timedOut )
        {
            ifLayout.getBackground().setColorFilter( getResources().getColor( R.color.then ), PorterDuff.Mode.SRC_ATOP);
            correctImageView.setVisibility( View.VISIBLE );
            errorImageView.setVisibility( View.GONE );
        }
        else
        {
            ifLayout.getBackground().setColorFilter( getResources().getColor( R.color.if_color ), PorterDuff.Mode.SRC_ATOP);
            correctImageView.setVisibility( View.GONE );
            errorImageView.setVisibility( View.VISIBLE );
        }

        mPlayButton.setText( "Play again" );
    }


    protected void startTimer( final int offset )
    {
        running = true;
        finished = false;
        timedOut = false;

        ifLayout.setBackgroundResource( 0 );

        updateTestUI();

        mProgressBar.setProgress( lapsedProgressTicks );
        mProgressTextView.setText( Integer.toString( PROGRESS_STEPS - lapsedProgressTicks - 1 ) );
        correctImageView.setVisibility( View.GONE );
        errorImageView.setVisibility( View.GONE );
        mProgressBar.setVisibility( View.VISIBLE );
        mProgressTextView.setVisibility( View.VISIBLE );

//        int duration = TOTAL_MILLIS - offset;
        int duration = TOTAL_MILLIS - ( lapsedProgressTicks * INTERVAL_MILLIS );
        mCountDownTimer = new CountDownTimer( duration, INTERVAL_MILLIS )
        {
            int steps = PROGRESS_STEPS - lapsedProgressTicks;
            @Override
            public void onTick( long millisUntilFinished )
            {
                Log.v( "Log_tag", "Tick of Progress" + lapsedProgressTicks + millisUntilFinished );
                timeLapsed = (int) millisUntilFinished + offset;
                lapsedProgressTicks++;
                int progress = (int) lapsedProgressTicks * 100 / ( TOTAL_MILLIS / INTERVAL_MILLIS );
                mProgressBar.setProgress( progress );
                mProgressTextView.setText( Integer.toString( steps -- ) );
            }

            @Override
            public void onFinish() {
                lapsedProgressTicks++;
                mProgressBar.setProgress(100);
                mProgressTextView.setText( "0" );
                mPlayButton.setVisibility( View.VISIBLE );
                mProgressBar.setVisibility( View.GONE );
                mProgressTextView.setVisibility( View.GONE );
                correctImageView.setVisibility( View.GONE );
                errorImageView.setVisibility( View.VISIBLE );
                finished = true;
                running = false;
                mPlayButton.setText( "Play again" );
                timedOut = true;
            }
        };
        mCountDownTimer.start();
    }

    protected void loadTest()
    {
        InputStream is = getResources().openRawResource( R.raw.edu_tests);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try
        {
            try
            {
                Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
                int n;
                while( ( n = reader.read( buffer ) ) != -1 )
                {
                    writer.write( buffer, 0, n );
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
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

        try
        {
            String jsonString = writer.toString();
            JSONObject jObject = new JSONObject( jsonString );
            JSONArray jArray = jObject.getJSONArray( "tests" );

            //pick a random test
            Random r = new Random();
            testIndex = r.nextInt( jArray.length() );
            test = jArray.getJSONObject( testIndex );

            if1Right = r.nextBoolean();

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }

    private void updateTestUI()
    {
        ifStatementTextView.setText( "" );
        try
        {
            String then = test.getString( "then" );
            String wrong = test.getString( "wrong" );
            String right = test.getString( "right" );

            thenStatementTextView.setText( then );

            if( if1Right )
            {
                if2Button.setText( wrong );
                if1Button.setText( right );
            }
            else
            {
                if1Button.setText( wrong );
                if2Button.setText( right );
            }
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
    }

}
