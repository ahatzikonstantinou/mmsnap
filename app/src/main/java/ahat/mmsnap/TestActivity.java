package ahat.mmsnap;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class TestActivity extends AppCompatActivity
{

    ProgressBar mProgressBar;
    int i=0;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_test );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        mProgressBar = (ProgressBar) findViewById( R.id.edu_test_progressBar );

        startTimer();

    }

    protected void startTimer()
    {
        CountDownTimer mCountDownTimer;

        mProgressBar.setProgress(i);
        mCountDownTimer = new CountDownTimer(20000,1000)
        {
            @Override
            public void onTick( long millisUntilFinished )
            {
                Log.v( "Log_tag", "Tick of Progress" + i + millisUntilFinished );
                i++;
                mProgressBar.setProgress( (int) i * 100 / ( 20000 / 1000 ) );

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                mProgressBar.setProgress(100);
            }
        };
        mCountDownTimer.start();
    }

}
